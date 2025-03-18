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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.ArticleLikeDO;
import edu.neuq.techhub.domain.entity.UserStatsDO;
import edu.neuq.techhub.domain.enums.LikeStatusEnum;
import edu.neuq.techhub.domain.enums.article.ArticleStatusEnum;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleLikeMapper;
import edu.neuq.techhub.mapper.ArticleMapper;
import edu.neuq.techhub.mapper.UserStatsMapper;
import edu.neuq.techhub.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author panda
* @description 针对表【sys_article_like(文章点赞表)】的数据库操作Service实现
* @createDate 2025-03-18 15:32:21
*/
@Service
@RequiredArgsConstructor
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLikeDO>
        implements
            ArticleLikeService {

    private final ArticleMapper articleMapper;
    private final UserStatsMapper userStatsMapper;
    private final ArticleLikeMapper articleLikeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer likeArticle(Long userId, Long articleId) {
        // 校验文章是否存在
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO article = articleMapper.selectById(articleId);
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR, "文章不存在");

        // 校验文章是否通过审核
        ThrowUtils.throwIf(!article.getStatus().equals(ArticleStatusEnum.REVIEW_PASSED.getCode()),
                ErrorCode.OPERATION_ERROR, "文章没有过审");

        // 查询点赞关系是否存在
        LambdaQueryWrapper<ArticleLikeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLikeDO::getUserId, userId)
                .eq(ArticleLikeDO::getArticleId, articleId);
        ArticleLikeDO articleLikeDO = this.getOne(queryWrapper);

        // 如果点赞关系不存在，创建新的点赞记录
        if (articleLikeDO == null) {
            articleLikeDO = new ArticleLikeDO();
            articleLikeDO.setUserId(userId);
            articleLikeDO.setArticleId(articleId);
            articleLikeDO.setStatus(LikeStatusEnum.LIKE.getCode());
            boolean insert = this.save(articleLikeDO);
            ThrowUtils.throwIf(!insert, ErrorCode.SYSTEM_ERROR);

            // 增加点赞计数
            updateLikeCount(articleId, userId, 1);
            return LikeStatusEnum.LIKE.getCode();
        }

        // 如果已经点赞，则取消点赞；如果未点赞，则添加点赞
        int currentStatus = articleLikeDO.getStatus();
        int newStatus = (currentStatus == LikeStatusEnum.LIKE.getCode()) ? LikeStatusEnum.UNLIKE.getCode() : LikeStatusEnum.LIKE.getCode();
        int delta = (newStatus == LikeStatusEnum.LIKE.getCode()) ? 1 : -1;

        // 更新点赞状态
        articleLikeDO.setStatus(newStatus);
        int update = articleLikeMapper.updateById(articleLikeDO);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);

        // 更新点赞计数
        updateLikeCount(articleId, userId, delta);

        return newStatus;
    }

    /**
     * 更新点赞计数
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param delta 变化量(+1或-1)
     */
    private void updateLikeCount(Long articleId, Long userId, int delta) {
        // 更新文章点赞数
        LambdaUpdateWrapper<ArticleDO> articleWrapper = new LambdaUpdateWrapper<>();
        if (delta > 0) {
            articleWrapper.setIncrBy(ArticleDO::getLikeCount, delta);
        } else {
            articleWrapper.setDecrBy(ArticleDO::getLikeCount, -delta);
        }
        articleWrapper.eq(ArticleDO::getId, articleId);
        int update = articleMapper.update(articleWrapper);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);

        // 更新用户点赞统计
        LambdaUpdateWrapper<UserStatsDO> userWrapper = new LambdaUpdateWrapper<>();
        if (delta > 0) {
            userWrapper.setIncrBy(UserStatsDO::getLikeCount, delta);
        } else {
            userWrapper.setDecrBy(UserStatsDO::getLikeCount, -delta);
        }
        userWrapper.eq(UserStatsDO::getId, userId); // 添加缺失的用户ID条件
        update = userStatsMapper.update(userWrapper);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
    }
}
