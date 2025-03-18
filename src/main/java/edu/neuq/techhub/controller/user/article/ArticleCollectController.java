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

import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.ArticleCollectService;
import edu.neuq.techhub.utils.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
@Tag(name = "文章收藏接口")
public class ArticleCollectController {

    private final ArticleCollectService articleCollectService;

    @Operation(summary = "文章收藏 / 取消收藏")
    @PostMapping("/{id}/collect")
    public BaseResponse<Integer> collectArticle(@PathVariable Long id) {
        LoginUserVO loginUser = UserUtils.getLoginUser();
        Integer result = articleCollectService.collectArticle(loginUser.getId(), id);
        return ResultUtils.success(result);
    }

}