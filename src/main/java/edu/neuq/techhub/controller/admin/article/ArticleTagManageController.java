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

import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.tag.ArticleTagAddDTO;
import edu.neuq.techhub.service.ArticleTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/article/tags")
@RequiredArgsConstructor
@Tag(name = "文章标签管理")
public class ArticleTagManageController {

    private final ArticleTagService articleTagService;

    @Operation(summary = "添加文章标签")
    @PostMapping
    public BaseResponse<Long> addArticleTag(@RequestBody ArticleTagAddDTO articleTagAddDTO) {
        Long tagId = articleTagService.addArticleTag(articleTagAddDTO);
        return ResultUtils.success(tagId);
    }

    @Operation(summary = "删除文章标签")
    @DeleteMapping("/{id}")
    public BaseResponse<Integer> removeArticleTagById(@PathVariable Long id) {
        articleTagService.removeArticleTagById(id);
        return ResultUtils.success(0);
    }

}