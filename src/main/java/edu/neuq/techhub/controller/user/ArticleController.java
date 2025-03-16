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

package edu.neuq.techhub.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        Long id = articleService.createDraft(loginUserVO.getId());
        return ResultUtils.success(id);
    }

    @PatchMapping("/draft")
    @Operation(summary = "保存文章草稿")
    public BaseResponse<Integer> saveDraft(@RequestBody ArticleDraftUpdateDTO articleDraftUpdateDTO) {
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        articleService.saveDraft(articleDraftUpdateDTO, loginUserVO.getId());
        return ResultUtils.success(0);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布文章")
    public BaseResponse<Integer> publishArticle(@RequestBody ArticleDraftUpdateDTO articleDraftUpdateDTO) {
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        articleService.publishArticle(articleDraftUpdateDTO, loginUserVO.getId());
        return ResultUtils.success(0);
    }

}