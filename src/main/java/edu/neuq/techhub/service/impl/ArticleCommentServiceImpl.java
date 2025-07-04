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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.article.comment.ArticleCommentAddDTO;
import edu.neuq.techhub.domain.dto.article.comment.ArticleCommentQueryDTO;
import edu.neuq.techhub.domain.entity.ArticleCommentDO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.article.ArticleStatusEnum;
import edu.neuq.techhub.domain.vo.article.ArticleCommentVO;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleCommentMapper;
import edu.neuq.techhub.mapper.ArticleMapper;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.service.ArticleCommentService;
import edu.neuq.techhub.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleCommentDO>
        implements
            ArticleCommentService {

    private final ArticleMapper articleMapper;

    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        // 文章评论数 +1
        LambdaUpdateWrapper<ArticleDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ArticleDO::getId, articleCommentDO.getArticleId());
        updateWrapper.setIncrBy(true, ArticleDO::getCommentCount, 1);
        int update = articleMapper.update(updateWrapper);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
        return articleCommentDO.getId();
    }

    @Override
    public Page<ArticleCommentVO> listCommentsByPage(ArticleCommentQueryDTO articleCommentQueryDTO) {
        // 参数校验
        validateParams(articleCommentQueryDTO);

        // 分页查询一级评论
        Page<ArticleCommentDO> firstCommentDOPage = queryFirstLevelComments(articleCommentQueryDTO);

        // 查询所有二级评论
        Map<Long, List<ArticleCommentVO>> secondCommentMap = querySecondLevelComments(firstCommentDOPage);

        // 查询所有相关用户信息
        Map<Long, UserDO> userMap = queryUserInfo(firstCommentDOPage, secondCommentMap);

        // 构建返回结果
        return buildResultPage(firstCommentDOPage, secondCommentMap, userMap);
    }

    private void validateParams(ArticleCommentQueryDTO articleCommentQueryDTO) {
        ThrowUtils.throwIf(articleCommentQueryDTO == null, ErrorCode.PARAMS_ERROR, "参数不能为空");

        Long articleId = articleCommentQueryDTO.getArticleId();
        ArticleDO articleDO = articleMapper.selectById(articleId);
        ThrowUtils.throwIf(articleDO == null, ErrorCode.PARAMS_ERROR, "文章不存在");
        ThrowUtils.throwIf(!articleDO.getStatus().equals(ArticleStatusEnum.REVIEW_PASSED.getCode()),
                ErrorCode.PARAMS_ERROR, "文章没有过审");

        int current = articleCommentQueryDTO.getCurrent();
        int size = articleCommentQueryDTO.getSize();
        ThrowUtils.throwIf(size <= 0 || size > 20 || current < 1, ErrorCode.PARAMS_ERROR, "分页参数不合法");
    }

    private Page<ArticleCommentDO> queryFirstLevelComments(ArticleCommentQueryDTO articleCommentQueryDTO) {
        LambdaQueryWrapper<ArticleCommentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleCommentDO::getArticleId, articleCommentQueryDTO.getArticleId())
                .isNull(ArticleCommentDO::getParentId)
                .orderByDesc(ArticleCommentDO::getCreateTime);

        return this.page(articleCommentQueryDTO.toMpPage(), queryWrapper);
    }

    private Map<Long, List<ArticleCommentVO>> querySecondLevelComments(Page<ArticleCommentDO> firstCommentDOPage) {
        List<Long> firstCommentIds = firstCommentDOPage.getRecords().stream()
                .map(ArticleCommentDO::getId)
                .collect(Collectors.toList());

        if (firstCommentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<ArticleCommentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ArticleCommentDO::getParentId, firstCommentIds);

        List<ArticleCommentDO> secondCommentList = this.list(queryWrapper);

        return secondCommentList.stream()
                .map(ArticleCommentVO::obj2vo)
                .collect(Collectors.groupingBy(
                        ArticleCommentVO::getParentId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    list.sort(Comparator.comparing(ArticleCommentVO::getCreateTime));
                                    return list;
                                })));
    }

    private Map<Long, UserDO> queryUserInfo(Page<ArticleCommentDO> firstCommentDOPage,
                                            Map<Long, List<ArticleCommentVO>> secondCommentMap) {
        // 收集所有需要查询的用户ID
        Set<Long> userIds = new HashSet<>();

        // 添加一级评论用户ID
        firstCommentDOPage.getRecords().forEach(comment -> userIds.add(comment.getUserId()));

        // 添加二级评论用户ID和回复用户ID
        secondCommentMap.values().stream()
                .flatMap(Collection::stream)
                .forEach(comment -> {
                    userIds.add(comment.getUserId());
                    if (comment.getReplyUserId() != null) {
                        userIds.add(comment.getReplyUserId());
                    }
                });

        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量查询用户信息
        return userMapper.selectByIds(userIds).stream()
                .collect(Collectors.toMap(
                        UserDO::getId,
                        Function.identity(),
                        (existing, replacement) -> existing));
    }

    private Page<ArticleCommentVO> buildResultPage(Page<ArticleCommentDO> firstCommentDOPage,
                                                   Map<Long, List<ArticleCommentVO>> secondCommentMap,
                                                   Map<Long, UserDO> userMap) {
        Page<ArticleCommentVO> result = new Page<>();
        BeanUtil.copyProperties(firstCommentDOPage, result);

        List<ArticleCommentVO> firstCommentVOList = firstCommentDOPage.getRecords().stream()
                .map(ArticleCommentVO::obj2vo)
                .collect(Collectors.toList());

        // 填充评论信息
        firstCommentVOList.forEach(articleCommentVO -> {
            // 填充一级评论用户信息
            UserDO userDO = userMap.get(articleCommentVO.getUserId());
            if (userDO != null) {
                articleCommentVO.setNickname(userDO.getNickname());
                articleCommentVO.setAvatar(userDO.getAvatar());
            }

            // 填充二级评论及用户信息
            List<ArticleCommentVO> children = secondCommentMap.get(articleCommentVO.getId());
            articleCommentVO.setChildren(children != null ? children : new ArrayList<>());

            if (children != null) {
                // 填充一级评论回复数
                articleCommentVO.setReplyCount(children.size());
                // 填充每一个子评论
                children.forEach(childComment -> {
                    // 填充评论者信息
                    UserDO user = userMap.get(childComment.getUserId());
                    if (user != null) {
                        childComment.setNickname(user.getNickname());
                        childComment.setAvatar(user.getAvatar());
                    }

                    // 填充回复对象信息
                    if (childComment.getReplyUserId() != null) {
                        UserDO replyUser = userMap.get(childComment.getReplyUserId());
                        if (replyUser != null) {
                            childComment.setReplyNickname(replyUser.getNickname());
                        }
                    }
                });
            }
        });

        result.setRecords(firstCommentVOList);
        return result;
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
