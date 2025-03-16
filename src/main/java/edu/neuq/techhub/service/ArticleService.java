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

package edu.neuq.techhub.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.domain.dto.article.ArticleQueryDTO;
import edu.neuq.techhub.domain.entity.ArticleDO;

/**
* @author panda
* @description 针对表【sys_article(文章表)】的数据库操作Service
* @createDate 2025-03-16 12:34:59
*/
public interface ArticleService extends IService<ArticleDO> {

    Long createDraft(Long userId);

    void saveDraft(ArticleDraftUpdateDTO articleDraftUpdateDTO, Long userId);

    void publishArticle(ArticleDraftUpdateDTO articleDraftUpdateDTO, Long userId);

    Page<ArticleDO> listArticleByPage(ArticleQueryDTO articleQueryDTO);

    void passArticle(Long articleId, Long userId);

    void rejectArticle(Long articleId, String message, Long userId);
}
