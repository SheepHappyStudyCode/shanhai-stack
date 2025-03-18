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
import edu.neuq.techhub.domain.entity.ArticleCollectDO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.UserStatsDO;
import edu.neuq.techhub.domain.enums.article.ArticleStatusEnum;
import edu.neuq.techhub.domain.enums.article.CollectStatusEnum;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleCollectMapper;
import edu.neuq.techhub.mapper.ArticleMapper;
import edu.neuq.techhub.mapper.UserStatsMapper;
import edu.neuq.techhub.service.ArticleCollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author panda
 * @description 针对表【sys_article_collect(文章收藏表)】的数据库操作Service实现
 * @createDate 2025-03-18 16:43:54
 */
@Service
@RequiredArgsConstructor
public class ArticleCollectServiceImpl extends ServiceImpl<ArticleCollectMapper, ArticleCollectDO>
        implements
            ArticleCollectService {

    private final ArticleMapper articleMapper;
    private final UserStatsMapper userStatsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer collectArticle(Long userId, Long articleId) {
        // 校验文章是否存在
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO article = articleMapper.selectById(articleId);
        ThrowUtils.throwIf(article == null, ErrorCode.PARAMS_ERROR, "文章不存在");

        // 校验文章是否通过审核
        ThrowUtils.throwIf(!article.getStatus().equals(ArticleStatusEnum.REVIEW_PASSED.getCode()),
                ErrorCode.OPERATION_ERROR, "文章没有过审");

        // 查询收藏关系是否存在
        LambdaQueryWrapper<ArticleCollectDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleCollectDO::getUserId, userId)
                .eq(ArticleCollectDO::getArticleId, articleId);
        ArticleCollectDO articleCollectDO = this.getOne(queryWrapper);

        // 如果收藏关系不存在，创建新的收藏记录
        if (articleCollectDO == null) {
            articleCollectDO = new ArticleCollectDO();
            articleCollectDO.setUserId(userId);
            articleCollectDO.setArticleId(articleId);
            articleCollectDO.setStatus(CollectStatusEnum.COLLECT.getCode());
            boolean insert = this.save(articleCollectDO);
            ThrowUtils.throwIf(!insert, ErrorCode.SYSTEM_ERROR);

            // 增加收藏计数
            updateCollectCount(articleId, userId, 1);
            return CollectStatusEnum.COLLECT.getCode();
        }

        // 如果已经收藏，则取消收藏；如果未收藏，则添加收藏
        int currentStatus = articleCollectDO.getStatus();
        int newStatus = (currentStatus == CollectStatusEnum.COLLECT.getCode()) ? CollectStatusEnum.CANCEL.getCode() : CollectStatusEnum.COLLECT.getCode();
        int delta = (newStatus == CollectStatusEnum.COLLECT.getCode()) ? 1 : -1;

        // 更新收藏状态
        articleCollectDO.setStatus(newStatus);
        boolean update = updateById(articleCollectDO);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);

        // 更新收藏计数
        updateCollectCount(articleId, userId, delta);

        return newStatus;
    }

    /**
     * 更新收藏计数
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param delta 变化量(+1或-1)
     */
    private void updateCollectCount(Long articleId, Long userId, int delta) {
        // 更新文章收藏数
        LambdaUpdateWrapper<ArticleDO> articleWrapper = new LambdaUpdateWrapper<>();
        if (delta > 0) {
            articleWrapper.setIncrBy(ArticleDO::getCollectCount, delta);
        } else {
            articleWrapper.setDecrBy(ArticleDO::getCollectCount, -delta);
        }
        articleWrapper.eq(ArticleDO::getId, articleId);
        int update = articleMapper.update(articleWrapper);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);

        // 更新用户收藏统计
        LambdaUpdateWrapper<UserStatsDO> userWrapper = new LambdaUpdateWrapper<>();
        if (delta > 0) {
            userWrapper.setIncrBy(UserStatsDO::getCollectCount, delta);
        } else {
            userWrapper.setDecrBy(UserStatsDO::getCollectCount, -delta);
        }
        userWrapper.eq(UserStatsDO::getId, userId);
        update = userStatsMapper.update(userWrapper);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
    }
}