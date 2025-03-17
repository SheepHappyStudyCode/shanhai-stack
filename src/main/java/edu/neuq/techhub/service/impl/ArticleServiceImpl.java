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
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.common.CursorPageResult;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.domain.dto.article.ArticleQueryDTO;
import edu.neuq.techhub.domain.dto.article.ArticleSearchDTO;
import edu.neuq.techhub.domain.entity.ArticleCategoryDO;
import edu.neuq.techhub.domain.entity.ArticleContentDO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.article.ArticleSortFieldEnum;
import edu.neuq.techhub.domain.enums.article.ArticleStatusEnum;
import edu.neuq.techhub.domain.enums.user.UserRoleEnum;
import edu.neuq.techhub.domain.vo.article.ArticleDetailVO;
import edu.neuq.techhub.domain.vo.article.ArticleVO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.domain.vo.user.UserVO;
import edu.neuq.techhub.exception.BusinessException;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleCategoryMapper;
import edu.neuq.techhub.mapper.ArticleContentMapper;
import edu.neuq.techhub.mapper.ArticleMapper;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author panda
 * @description 针对表【sys_article(文章表)】的数据库操作Service实现
 * @createDate 2025-03-16 12:34:59
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleDO>
        implements
            ArticleService {

    private final ArticleContentMapper articleContentMapper;
    private final ArticleCategoryMapper articleCategoryMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDraft(Long userId) {
        ArticleDO articleDO = new ArticleDO();
        articleDO.setUserId(userId);
        articleDO.setEditTime(new Date());
        articleDO.setStatus(ArticleStatusEnum.DRAFT.getCode());
        boolean result = this.save(articleDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        Long articleId = articleDO.getId();
        ArticleContentDO articleContentDO = new ArticleContentDO();
        articleContentDO.setId(articleId);
        int insert = articleContentMapper.insert(articleContentDO);
        ThrowUtils.throwIf(insert != 1, ErrorCode.SYSTEM_ERROR);
        return articleId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDraft(ArticleDraftUpdateDTO articleDraftUpdateDTO, Long userId) {
        // 文章是否存在
        Long articleId = articleDraftUpdateDTO.getId();
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO originArticle = this.getById(articleId);
        ThrowUtils.throwIf(originArticle == null, ErrorCode.PARAMS_ERROR, "草稿不存在");
        // 文章是否由本人创建
        ThrowUtils.throwIf(!originArticle.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR);
        // 保存文章
        ArticleDO updateArticleDO = new ArticleDO();
        BeanUtils.copyProperties(articleDraftUpdateDTO, updateArticleDO);
        updateArticleDO.setEditTime(new Date());
        updateArticleDO.setTags(JSONUtil.toJsonStr(articleDraftUpdateDTO.getTagList()));
        boolean result = this.updateById(updateArticleDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        ArticleContentDO articleContentDO = new ArticleContentDO();
        articleContentDO.setId(articleId);
        articleContentDO.setContentHtml(articleDraftUpdateDTO.getContentHtml());
        articleContentDO.setContentMd(articleDraftUpdateDTO.getContentMd());
        int update = articleContentMapper.updateById(articleContentDO);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(ArticleDraftUpdateDTO articleDraftUpdateDTO, Long userId) {
        // 文章是否存在
        Long articleId = articleDraftUpdateDTO.getId();
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO originArticle = this.getById(articleId);
        ThrowUtils.throwIf(originArticle == null, ErrorCode.PARAMS_ERROR, "草稿不存在");
        ThrowUtils.throwIf(!originArticle.getStatus().equals(ArticleStatusEnum.DRAFT.getCode()), ErrorCode.PARAMS_ERROR, "只有处在草稿状态的文章才能上传");
        // 文章是否由本人创建
        ThrowUtils.throwIf(!originArticle.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR);
        // 校验文章参数是否合法
        validate(articleDraftUpdateDTO);
        // 保存文章
        ArticleDO updateArticleDO = new ArticleDO();
        BeanUtils.copyProperties(articleDraftUpdateDTO, updateArticleDO);
        updateArticleDO.setEditTime(new Date());
        updateArticleDO.setTags(JSONUtil.toJsonStr(articleDraftUpdateDTO.getTagList()));
        updateArticleDO.setStatus(ArticleStatusEnum.PUBLISHED.getCode());
        updateArticleDO.setPublishTime(new Date());
        boolean result = this.updateById(updateArticleDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        ArticleContentDO articleContentDO = new ArticleContentDO();
        articleContentDO.setId(articleId);
        articleContentDO.setContentHtml(articleDraftUpdateDTO.getContentHtml());
        articleContentDO.setContentMd(articleDraftUpdateDTO.getContentMd());
        int update = articleContentMapper.updateById(articleContentDO);
        ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public Page<ArticleDO> listArticleByPage(ArticleQueryDTO articleQueryDTO, LoginUserVO loginUserVO) {
        ThrowUtils.throwIf(articleQueryDTO == null, ErrorCode.PARAMS_ERROR);
        int current = articleQueryDTO.getCurrent();
        int size = articleQueryDTO.getSize();
        ThrowUtils.throwIf(current < 1 || size < 0 || size > 20, ErrorCode.PARAMS_ERROR, "分页参数不合法");
        LambdaQueryWrapper<ArticleDO> queryWrapper = buildQueryWrapper(articleQueryDTO, loginUserVO);
        Page<ArticleDO> queryPage = articleQueryDTO.toMpPage();
        return this.page(queryPage, queryWrapper);
    }

    @Override
    public void passArticle(Long articleId, Long userId) {
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO articleDO = this.getById(articleId);
        ThrowUtils.throwIf(articleDO == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        ThrowUtils.throwIf(articleDO.getStatus().equals(ArticleStatusEnum.DRAFT.getCode()), ErrorCode.NOT_FOUND_ERROR, "草稿状态的文章不允许审核");
        ThrowUtils.throwIf(articleDO.getStatus().equals(ArticleStatusEnum.REVIEW_PASSED.getCode()), ErrorCode.NOT_FOUND_ERROR, "重复审核");
        ArticleDO updateArticle = new ArticleDO();
        updateArticle.setId(articleId);
        updateArticle.setStatus(ArticleStatusEnum.REVIEW_PASSED.getCode());
        updateArticle.setReviewerId(userId);
        updateArticle.setReviewTime(new Date());
        this.updateById(updateArticle);
    }

    @Override
    public void rejectArticle(Long articleId, String message, Long userId) {
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO articleDO = this.getById(articleId);
        ThrowUtils.throwIf(articleDO == null, ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        ThrowUtils.throwIf(articleDO.getStatus().equals(ArticleStatusEnum.DRAFT.getCode()), ErrorCode.NOT_FOUND_ERROR, "草稿状态的文章不允许审核");
        ThrowUtils.throwIf(articleDO.getStatus().equals(ArticleStatusEnum.REVIEW_REJECTED.getCode()), ErrorCode.NOT_FOUND_ERROR, "重复审核");
        ArticleDO updateArticle = new ArticleDO();
        updateArticle.setId(articleId);
        updateArticle.setStatus(ArticleStatusEnum.REVIEW_REJECTED.getCode());
        updateArticle.setReviewerId(userId);
        updateArticle.setReviewTime(new Date());
        updateArticle.setReviewMessage(message);
        this.updateById(updateArticle);
    }

    @Override
    public ArticleDetailVO getArticleDetailById(Long articleId, LoginUserVO loginUserVO) {
        // 参数校验
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);

        // 查询文章
        ArticleDO articleDO = Optional.ofNullable(this.getById(articleId))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在"));

        // 权限校验
        validateArticleAccess(articleDO, loginUserVO);

        // 构造返回对象
        return buildArticleDetailVO(articleDO);
    }

    @Override
    public CursorPageResult<ArticleVO, ArticleSearchDTO.ArticleCursor> listArticleByCursorPage(ArticleSearchDTO articleSearchDTO) {
        // 参数校验
        ThrowUtils.throwIf(articleSearchDTO == null, ErrorCode.PARAMS_ERROR);
        int size = articleSearchDTO.getSize();
        ThrowUtils.throwIf(size < 0 || size > 20, ErrorCode.PARAMS_ERROR, "分页参数不合法");
        String sortField = articleSearchDTO.getSortField();
        ArticleSortFieldEnum sortFiledEnum = ArticleSortFieldEnum.getByValue(sortField);
        ThrowUtils.throwIf(sortFiledEnum == null, ErrorCode.PARAMS_ERROR, "暂不支持这种排序方式");

        // 构造查询条件
        QueryWrapper<ArticleDO> queryWrapper = buildQueryWrapper(articleSearchDTO);

        // 执行查询
        List<ArticleDO> articleList = this.list(queryWrapper);

        // 判断是否有下一页
        boolean hasMore = articleList.size() > size;
        if (hasMore) {
            // 移除多查询的一条记录
            articleList.remove(size);
        }

        // 转换为VO对象
        List<ArticleVO> articleVOList = articleList.parallelStream()
                .map(ArticleVO::obj2vo)
                .collect(Collectors.toList());
        fillArticleVOList(articleVOList);
        // 构造下一页的游标
        ArticleSearchDTO.ArticleCursor nextCursor = null;
        if (hasMore && !articleVOList.isEmpty()) {
            ArticleVO lastArticle = articleVOList.get(articleVOList.size() - 1);
            nextCursor = buildNextCursor(lastArticle, sortFiledEnum);
        }

        // 返回结果
        return CursorPageResult.of(articleVOList, hasMore, nextCursor);
    }

    @Override
    public Page<ArticleVO> listMyArticleByPage(ArticleQueryDTO articleQueryDTO, LoginUserVO loginUserVO) {
        Page<ArticleDO> articleDOPage = this.listArticleByPage(articleQueryDTO, loginUserVO);
        Page<ArticleVO> articleVOPage = new Page<>();
        BeanUtil.copyProperties(articleDOPage, articleVOPage);
        articleVOPage.setRecords(articleDOPage.getRecords().stream().map(ArticleVO::obj2vo).collect(Collectors.toList()));
        return articleVOPage;
    }

    @Override
    public void removeMyArticleById(Long articleId, Long userId) {
        // 校验文章是否存在
        ThrowUtils.throwIf(articleId == null || articleId <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO articleDO = this.getById(articleId);
        ThrowUtils.throwIf(articleDO == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验文章作者是否是自己
        ThrowUtils.throwIf(!articleDO.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR);
        // 删除文章
        boolean res = this.removeById(articleId);
        ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR);
    }

    /**
     * 填充文章VO列表中的分类名称和用户信息
     * @param articleVOList 需要填充信息的文章VO列表
     */
    public void fillArticleVOList(List<ArticleVO> articleVOList) {
        if (articleVOList == null || articleVOList.isEmpty()) {
            return;
        }

        // 收集需要查询的分类ID和用户ID
        Set<Long> categoryIds = articleVOList.stream()
                .map(ArticleVO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> userIds = articleVOList.stream()
                .map(ArticleVO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 查询数据并构建ID到对象的映射
        Map<Long, UserVO> userVOMap = userMapper.selectByIds(userIds).stream()
                .map(UserVO::obj2vo)
                .collect(Collectors.toMap(UserVO::getId, userVO -> userVO, (v1, v2) -> v1));

        Map<Long, ArticleCategoryDO> categoryMap = articleCategoryMapper.selectByIds(categoryIds).stream()
                .collect(Collectors.toMap(ArticleCategoryDO::getId, category -> category, (v1, v2) -> v1));

        // 填充每篇文章的分类名称和用户信息
        for (ArticleVO articleVO : articleVOList) {
            // 使用 Optional 处理分类信息
            Optional.ofNullable(articleVO.getCategoryId())
                    .map(categoryMap::get)
                    .map(ArticleCategoryDO::getName)
                    .ifPresent(articleVO::setCategoryName);

            // 使用 Optional 处理用户信息
            Optional.ofNullable(articleVO.getUserId())
                    .map(userVOMap::get)
                    .ifPresent(articleVO::setUserVO);
        }
    }

    private ArticleSearchDTO.ArticleCursor buildNextCursor(ArticleVO article, ArticleSortFieldEnum sortFiledEnum) {
        ArticleSearchDTO.ArticleCursor cursor = new ArticleSearchDTO.ArticleCursor();
        cursor.setArticleId(article.getId());

        // 根据排序字段设置对应值
        switch (sortFiledEnum) {
            case PUBLISH_TIME -> cursor.setPublishTime(article.getPublishTime());
            case LIKE_COUNT -> cursor.setLikeCount(article.getLikeCount());
            case COLLECT_COUNT -> cursor.setCollectCount(article.getCollectCount());
        }

        return cursor;
    }

    private QueryWrapper<ArticleDO> buildQueryWrapper(ArticleSearchDTO articleSearchDTO) {
        // 构造基础查询条件
        QueryWrapper<ArticleDO> queryWrapper = new QueryWrapper<>();
        String searchText = articleSearchDTO.getSearchText();
        Long userId = articleSearchDTO.getUserId();
        Long categoryId = articleSearchDTO.getCategoryId();
        String sortField = articleSearchDTO.getSortField();
        boolean asc = articleSearchDTO.isAsc();
        int size = articleSearchDTO.getSize();
        ArticleSortFieldEnum sortFiledEnum = ArticleSortFieldEnum.getByValue(sortField);

        // 构造过滤条件
        queryWrapper.and(StrUtil.isNotBlank(searchText), w -> w.like("title", searchText).or().like("summary", searchText));
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(categoryId != null, "category_id", categoryId);
        queryWrapper.eq("status", ArticleStatusEnum.REVIEW_PASSED.getCode());

        // 构造游标分页条件
        if (!articleSearchDTO.isFirstQuery()) {
            ArticleSearchDTO.ArticleCursor cursor = articleSearchDTO.getCursor();
            Long articleId = cursor.getArticleId();
            // 获取排序字段的值
            Object sortValue = getSortValue(cursor, sortFiledEnum);
            // 校验游标值
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(articleId, sortValue), ErrorCode.PARAMS_ERROR, "游标值不能为空");
            if (asc) {
                // 升序：(sortField > sortValue) OR (sortField = sortValue AND id > articleId)
                queryWrapper.and(w -> w
                        .gt(sortField, sortValue)
                        .or(o -> o.eq(sortField, sortValue).gt("id", articleId)));
            } else {
                // 降序：(sortField < sortValue) OR (sortField = sortValue AND id < articleId)
                queryWrapper.and(w -> w
                        .lt(sortField, sortValue)
                        .or(o -> o.eq(sortField, sortValue).lt("id", articleId)));
            }
        }

        // 添加排序
        queryWrapper.orderBy(true, asc, sortField);
        queryWrapper.orderBy(true, asc, "id"); // 确保结果稳定性

        // 限制返回数量
        queryWrapper.last(String.format("limit %d", size + 1));

        return queryWrapper;
    }

    private Object getSortValue(ArticleSearchDTO.ArticleCursor cursor, ArticleSortFieldEnum sortFiledEnum) {
        return switch (sortFiledEnum) {
            case PUBLISH_TIME -> cursor.getPublishTime();
            case LIKE_COUNT -> cursor.getLikeCount();
            case COLLECT_COUNT -> cursor.getCollectCount();
        };
    }

    private void validateArticleAccess(ArticleDO articleDO, LoginUserVO loginUserVO) {
        boolean isAuthor = loginUserVO != null && loginUserVO.getId().equals(articleDO.getUserId());
        boolean isAdmin = loginUserVO != null && UserRoleEnum.ADMIN.getValue().equals(loginUserVO.getRole());

        // 作者可以查看自己的所有文章
        if (isAuthor) {
            return;
        }

        // 管理员可以查看除草稿外的所有文章
        if (isAdmin) {
            ThrowUtils.throwIf(ArticleStatusEnum.DRAFT.getCode() == articleDO.getStatus(), ErrorCode.NO_AUTH_ERROR, "只有本人能查看文章草稿");
            return;
        }

        // 其他用户只能查看审核通过的文章
        ThrowUtils.throwIf(ArticleStatusEnum.REVIEW_PASSED.getCode() != articleDO.getStatus(), ErrorCode.NO_AUTH_ERROR, "只有审核通过的文章才能被访问");
    }

    private ArticleDetailVO buildArticleDetailVO(ArticleDO articleDO) {
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        BeanUtil.copyProperties(articleDO, articleDetailVO);

        // 并行查询相关数据
        CompletableFuture<ArticleContentDO> contentFuture = CompletableFuture.supplyAsync(() -> articleContentMapper.selectById(articleDO.getId()));

        CompletableFuture<ArticleCategoryDO> categoryFuture = CompletableFuture.supplyAsync(() -> {
            if (articleDO.getCategoryId() != null) {
                return articleCategoryMapper.selectById(articleDO.getCategoryId());
            }
            return null;
        });

        CompletableFuture<UserVO> userFuture = CompletableFuture.supplyAsync(() -> {
            UserDO userDO = userMapper.selectById(articleDO.getUserId());
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(userDO, userVO);
            return userVO;
        });

        try {
            // 等待所有异步操作完成
            CompletableFuture.allOf(contentFuture, categoryFuture, userFuture).join();

            // 设置文章内容
            ArticleContentDO content = contentFuture.get();
            articleDetailVO.setContentHtml(content.getContentHtml());
            articleDetailVO.setContentMd(content.getContentMd());

            // 设置分类名称
            articleDetailVO.setCategoryName(Optional.ofNullable(categoryFuture.get()).map(ArticleCategoryDO::getName).orElse(null));

            // 设置用户信息
            articleDetailVO.setUserVO(userFuture.get());

            // 转换标签
            articleDetailVO.setTagList(JSONUtil.toList(articleDO.getTags(), String.class));

        } catch (InterruptedException | ExecutionException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取文章详情失败");
        }

        return articleDetailVO;
    }

    private LambdaQueryWrapper<ArticleDO> buildQueryWrapper(ArticleQueryDTO articleQueryDTO, LoginUserVO loginUserVO) {
        LambdaQueryWrapper<ArticleDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long userId = articleQueryDTO.getUserId();
        String title = articleQueryDTO.getTitle();
        String summary = articleQueryDTO.getSummary();
        Long categoryId = articleQueryDTO.getCategoryId();
        String tag = articleQueryDTO.getTag();
        Integer minReadTime = articleQueryDTO.getMinReadTime();
        Integer maxReadTime = articleQueryDTO.getMaxReadTime();
        Integer isOriginal = articleQueryDTO.getIsOriginal();
        Integer status = articleQueryDTO.getStatus();

        lambdaQueryWrapper.eq(userId != null, ArticleDO::getUserId, userId);
        lambdaQueryWrapper.like(StrUtil.isNotBlank(title), ArticleDO::getTitle, title);
        lambdaQueryWrapper.like(StrUtil.isNotBlank(summary), ArticleDO::getSummary, summary);
        lambdaQueryWrapper.eq(categoryId != null, ArticleDO::getCategoryId, categoryId);
        lambdaQueryWrapper.like(StrUtil.isNotBlank(tag), ArticleDO::getTags, tag);
        lambdaQueryWrapper.ge(minReadTime != null, ArticleDO::getReadTime, minReadTime);
        lambdaQueryWrapper.le(maxReadTime != null, ArticleDO::getReadTime, maxReadTime);
        lambdaQueryWrapper.eq(isOriginal != null, ArticleDO::getIsOriginal, isOriginal);
        lambdaQueryWrapper.eq(status != null, ArticleDO::getStatus, status);
        // 只有本人能查看草稿
        if (!loginUserVO.getId().equals(articleQueryDTO.getUserId())) {
            lambdaQueryWrapper.ne(ArticleDO::getStatus, ArticleStatusEnum.DRAFT.getCode());
        }

        return lambdaQueryWrapper;

    }

    void validate(ArticleDraftUpdateDTO articleDraftUpdateDTO) {
        String title = articleDraftUpdateDTO.getTitle();
        String summary = articleDraftUpdateDTO.getSummary();
        String contentHtml = articleDraftUpdateDTO.getContentHtml();
        String contentMd = articleDraftUpdateDTO.getContentMd();
        Long categoryId = articleDraftUpdateDTO.getCategoryId();
        List<String> tagList = articleDraftUpdateDTO.getTagList();
        Integer readTime = articleDraftUpdateDTO.getReadTime();
        Integer isOriginal = articleDraftUpdateDTO.getIsOriginal();
        String originalUrl = articleDraftUpdateDTO.getOriginalUrl();

        // 非空校验
        ThrowUtils.throwIf(StrUtil.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(summary), ErrorCode.PARAMS_ERROR, "文章简介不能为空");
        ThrowUtils.throwIf(StrUtil.hasBlank(contentHtml, contentMd), ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        ThrowUtils.throwIf(categoryId == null, ErrorCode.PARAMS_ERROR, "文章分类不能为空");
        ThrowUtils.throwIf(CollectionUtil.isEmpty(tagList), ErrorCode.PARAMS_ERROR, "文章标签不能为空");
        ThrowUtils.throwIf(readTime == null, ErrorCode.PARAMS_ERROR, "文章阅读时间不能为空");
        ThrowUtils.throwIf(isOriginal == null || isOriginal < 0 || isOriginal > 1, ErrorCode.PARAMS_ERROR, "文章是否原创不能为空");

        // 单独参数校验
        ThrowUtils.throwIf(title.length() > 150, ErrorCode.PARAMS_ERROR, "文章标题太长");
        ThrowUtils.throwIf(summary.length() > 255, ErrorCode.PARAMS_ERROR, "文章简介太长");
        ArticleCategoryDO articleCategoryDO = articleCategoryMapper.selectById(categoryId);
        ThrowUtils.throwIf(articleCategoryDO == null, ErrorCode.PARAMS_ERROR, "文章分类不存在");
        ThrowUtils.throwIf(readTime <= 0, ErrorCode.PARAMS_ERROR, "文章阅读时间只能是正整数");
        if (isOriginal == 0) {
            ThrowUtils.throwIf(StrUtil.isBlank(originalUrl), ErrorCode.PARAMS_ERROR, "文章转载地址不能为空");
        }

    }
}
