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

import cn.dev33.satoken.stp.StpUtil;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.comment.ArticleCommentAddDTO;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.service.ArticleCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Tag(name = "文章评论接口")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/{articleId}/comments")
    @Operation(summary = "添加文章评论")
    public BaseResponse<Long> createComment(@RequestBody ArticleCommentAddDTO articleCommentAddDTO, @PathVariable("articleId") Long articleId) {
        ThrowUtils.throwIf(articleCommentAddDTO == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        articleCommentAddDTO.setArticleId(articleId);
        articleCommentAddDTO.setUserId(StpUtil.getLoginIdAsLong());
        Long id = articleCommentService.createComment(articleCommentAddDTO);
        return ResultUtils.success(id);
    }

}