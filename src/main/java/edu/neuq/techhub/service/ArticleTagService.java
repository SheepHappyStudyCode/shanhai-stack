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

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neuq.techhub.domain.dto.article.tag.ArticleTagAddDTO;
import edu.neuq.techhub.domain.entity.ArticleTagDO;
import edu.neuq.techhub.domain.vo.article.ArticleTagVO;

import java.util.List;

/**
* @author panda
* @description 针对表【sys_article_tag(文章标签表)】的数据库操作Service
* @createDate 2025-03-18 14:11:57
*/
public interface ArticleTagService extends IService<ArticleTagDO> {

    void removeArticleTagById(Long tagId);

    Long addArticleTag(ArticleTagAddDTO articleTagAddDTO);

    List<ArticleTagVO> getAllTags();
}
