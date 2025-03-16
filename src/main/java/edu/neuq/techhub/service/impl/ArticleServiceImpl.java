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
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.domain.dto.article.ArticleQueryDTO;
import edu.neuq.techhub.domain.entity.ArticleCategoryDO;
import edu.neuq.techhub.domain.entity.ArticleContentDO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.ArticleStatusEnum;
import edu.neuq.techhub.domain.enums.UserRoleEnum;
import edu.neuq.techhub.domain.vo.article.ArticleDetailVO;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public Page<ArticleDO> listArticleByPage(ArticleQueryDTO articleQueryDTO) {
        ThrowUtils.throwIf(articleQueryDTO == null, ErrorCode.PARAMS_ERROR);
        int current = articleQueryDTO.getCurrent();
        int size = articleQueryDTO.getSize();
        ThrowUtils.throwIf(current < 1 || size < 0 || size > 20, ErrorCode.PARAMS_ERROR, "分页参数不合法");
        LambdaQueryWrapper<ArticleDO> queryWrapper = buildQueryWrapper(articleQueryDTO);
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

    private LambdaQueryWrapper<ArticleDO> buildQueryWrapper(ArticleQueryDTO articleQueryDTO) {
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
        // 排除草稿
        lambdaQueryWrapper.ne(ArticleDO::getStatus, ArticleStatusEnum.DRAFT.getCode());
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
