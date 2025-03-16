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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.article.category.AddArticleCategoryDTO;
import edu.neuq.techhub.domain.entity.ArticleCategoryDO;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.service.ArticleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage/article/categories")
@RequiredArgsConstructor
@Tag(name = "文章分类管理模块", description = "文章分类的增删改查")
public class ArticleCategoryManageController {

    private final ArticleCategoryService articleCategoryService;

    @Operation(summary = "添加文章分类")
    @PostMapping
    public BaseResponse<Integer> addArticleCategory(@RequestBody AddArticleCategoryDTO addArticleCategoryDTO) {
        String name = addArticleCategoryDTO.getName();
        Integer sort = addArticleCategoryDTO.getSort();
        LambdaQueryWrapper<ArticleCategoryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleCategoryDO::getName, name);
        ArticleCategoryDO existCategory = articleCategoryService.getOne(queryWrapper);
        ThrowUtils.throwIf(existCategory != null, ErrorCode.OPERATION_ERROR, "文章分类名重复");
        ArticleCategoryDO articleCategoryDO = new ArticleCategoryDO();
        articleCategoryDO.setName(name);
        articleCategoryDO.setSort(sort);
        boolean result = articleCategoryService.save(articleCategoryDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(0);
    }

    @Operation(summary = "删除文章分类")
    @DeleteMapping("/{id}")
    public BaseResponse<Integer> removeArticleCategoryById(@PathVariable Long id) {
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        ArticleCategoryDO articleCategoryDO = articleCategoryService.getById(id);
        ThrowUtils.throwIf(articleCategoryDO == null, ErrorCode.OPERATION_ERROR, "文章分类不存在");
        ThrowUtils.throwIf(!articleCategoryService.removeById(id), ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(0);
    }

    @Operation(summary = "查询所有文章分类")
    @GetMapping
    public BaseResponse<List<ArticleCategoryDO>> getAllCategories() {
        List<ArticleCategoryDO> categoryDOList = articleCategoryService.lambdaQuery().orderByAsc(ArticleCategoryDO::getSort).list();
        return ResultUtils.success(categoryDOList);
    }

}