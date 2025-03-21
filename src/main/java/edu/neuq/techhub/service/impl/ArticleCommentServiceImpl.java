/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.neuq.techhub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.article.comment.ArticleCommentAddDTO;
import edu.neuq.techhub.domain.entity.ArticleCommentDO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.article.ArticleStatusEnum;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleCommentMapper;
import edu.neuq.techhub.mapper.ArticleMapper;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.service.ArticleCommentService;
import edu.neuq.techhub.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleCommentDO>
        implements
            ArticleCommentService {

    private final ArticleMapper articleMapper;

    private final UserMapper userMapper;

    @Override
    public Long createComment(ArticleCommentAddDTO articleCommentAddDTO) {
        // 校验评论参数
        validate(articleCommentAddDTO);
        // 复制属性
        ArticleCommentDO articleCommentDO = new ArticleCommentDO();
        BeanUtil.copyProperties(articleCommentAddDTO, articleCommentDO);
        // 填充 ip 信息
        String ip = IpUtils.getIp();
        articleCommentDO.setIp(ip);
        articleCommentDO.setIpSource(IpUtils.getIp2region(ip));
        // 保存数据
        this.save(articleCommentDO);
        return articleCommentDO.getId();
    }

    private void validate(ArticleCommentAddDTO articleCommentAddDTO) {
        Long articleId = articleCommentAddDTO.getArticleId();
        Long userId = articleCommentAddDTO.getUserId();
        Long replyUserId = articleCommentAddDTO.getReplyUserId();
        Long parentId = articleCommentAddDTO.getParentId();
        String content = articleCommentAddDTO.getContent();
        // 非空校验
        ThrowUtils.throwIf(ObjectUtil.hasEmpty(articleId, userId, content), ErrorCode.PARAMS_ERROR, "参数不能为空");

        // 参数校验
        ThrowUtils.throwIf(content.length() > 1024, ErrorCode.PARAMS_ERROR, "评论内容过长");

        // 存在性校验
        ArticleDO articleDO = articleMapper.selectById(articleId);
        ThrowUtils.throwIf(articleDO == null, ErrorCode.PARAMS_ERROR, "文章不存在");
        ThrowUtils.throwIf(!articleDO.getStatus().equals(ArticleStatusEnum.REVIEW_PASSED.getCode()), ErrorCode.PARAMS_ERROR, "文章没有过审");
        LambdaQueryWrapper<UserDO> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(UserDO::getId, userId);
        boolean exists = userMapper.exists(userQueryWrapper);
        ThrowUtils.throwIf(!exists, ErrorCode.PARAMS_ERROR, "用户不存在");
        if (replyUserId != null) {
            userQueryWrapper.clear();
            userQueryWrapper.eq(UserDO::getId, replyUserId);
            exists = userMapper.exists(userQueryWrapper);
            ThrowUtils.throwIf(!exists, ErrorCode.PARAMS_ERROR, "回复用户不存在");
        }
        if (parentId != null) {
            ArticleCommentDO parentComment = this.getById(parentId);
            ThrowUtils.throwIf(parentComment == null, ErrorCode.PARAMS_ERROR, "父级评论不存在");
            ThrowUtils.throwIf(parentComment.getParentId() != null, ErrorCode.PARAMS_ERROR, "最多支持二级评论");
        }
    }
}
