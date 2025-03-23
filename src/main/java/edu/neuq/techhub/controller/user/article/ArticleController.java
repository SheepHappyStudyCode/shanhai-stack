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

package edu.neuq.techhub.controller.user.article;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.CursorPageResult;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.domain.dto.article.ArticleQueryDTO;
import edu.neuq.techhub.domain.dto.article.ArticleSearchDTO;
import edu.neuq.techhub.domain.vo.article.ArticleDetailVO;
import edu.neuq.techhub.domain.vo.article.ArticleVO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.ArticleService;
import edu.neuq.techhub.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Tag(name = "文章模块")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/draft")
    @Operation(summary = "创建文章草稿")
    public BaseResponse<Long> createDraft() {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        Long id = articleService.createDraft(loginUser.getId());
        return ResultUtils.success(id);
    }

    @PatchMapping("/draft")
    @Operation(summary = "保存文章草稿")
    public BaseResponse<Integer> saveDraft(@RequestBody ArticleDraftUpdateDTO articleDraftUpdateDTO) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleService.saveDraft(articleDraftUpdateDTO, loginUser.getId());
        return ResultUtils.success(0);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布文章")
    public BaseResponse<Integer> publishArticle(@RequestBody ArticleDraftUpdateDTO articleDraftUpdateDTO) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleService.publishArticle(articleDraftUpdateDTO, loginUser);
        return ResultUtils.success(0);
    }

    @Operation(summary = "根据 id 查询文章")
    @GetMapping("/{id}")
    public BaseResponse<ArticleDetailVO> getArticleDetailById(@PathVariable Long id) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        ArticleDetailVO articleDetailVO = articleService.getArticleDetailById(id, loginUser);
        return ResultUtils.success(articleDetailVO);
    }

    @Operation(summary = "通过游标分页查询文章")
    @PostMapping("/search")
    @SaIgnore
    public BaseResponse<CursorPageResult<ArticleVO, ArticleSearchDTO.ArticleCursor>> listArticleByCursorPage(@RequestBody ArticleSearchDTO articleSearchDTO) {
        LoginUserVO loginUser = StpUtil.isLogin() ? UserUtils.getLoginUser() : null;
        CursorPageResult<ArticleVO, ArticleSearchDTO.ArticleCursor> result = articleService.listArticleByCursorPage(articleSearchDTO, loginUser);
        return ResultUtils.success(result);
    }

    @Operation(summary = "查询我的文章")
    @GetMapping("/my")
    public BaseResponse<Page<ArticleVO>> listArticleByCursorPage(@ParameterObject ArticleQueryDTO articleQueryDTO) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleQueryDTO.setUserId(loginUser.getId());
        Page<ArticleVO> result = articleService.listMyArticleByPage(articleQueryDTO, loginUser);
        return ResultUtils.success(result);
    }

    @Operation(summary = "删除我的文章")
    @DeleteMapping("/{id}")
    public BaseResponse<Integer> removeMyArticleById(@PathVariable Long id) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleService.removeMyArticleById(id, loginUser.getId());
        return ResultUtils.success(0);
    }

}