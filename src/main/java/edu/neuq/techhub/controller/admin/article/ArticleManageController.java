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

package edu.neuq.techhub.controller.admin.article;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.ArticleQueryDTO;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.domain.vo.article.ArticleDetailVO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.ArticleService;
import edu.neuq.techhub.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/articles")
@RequiredArgsConstructor
@Tag(name = "文章管理")
public class ArticleManageController {

    private final ArticleService articleService;

    @Operation(summary = "分页查询文章")
    @GetMapping
    public BaseResponse<Page<ArticleDO>> listArticleByPage(@ParameterObject ArticleQueryDTO articleQueryDTO) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        Page<ArticleDO> articlePageResult = articleService.listArticleByPage(articleQueryDTO, loginUser);
        return ResultUtils.success(articlePageResult);
    }

    @Operation(summary = "文章审核通过")
    @PostMapping("/{id}/pass")
    public BaseResponse<Integer> passArticle(@PathVariable Long id) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleService.passArticle(id, loginUser.getId());
        return ResultUtils.success(0);
    }

    @Operation(summary = "文章审核不通过")
    @PostMapping("/{id}/reject")
    public BaseResponse<Integer> rejectArticle(@PathVariable Long id, @RequestBody String message) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        articleService.rejectArticle(id, message, loginUser.getId());
        return ResultUtils.success(0);
    }

    @Operation(summary = "根据 id 查询文章")
    @GetMapping("/{id}")
    public BaseResponse<ArticleDetailVO> listArticleByPage(@PathVariable Long id) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        ArticleDetailVO articleDetailVO = articleService.getArticleDetailById(id, loginUser);
        return ResultUtils.success(articleDetailVO);
    }

}