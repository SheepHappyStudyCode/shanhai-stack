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

package edu.neuq.techhub.domain.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.neuq.techhub.common.CursorPageRequest;
import edu.neuq.techhub.domain.enums.article.ArticleSortFieldEnum;
import edu.neuq.techhub.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleSearchDTO extends CursorPageRequest<ArticleSearchDTO.ArticleCursor> {

    @Data
    public static class ArticleCursor {

        private Long articleId;
        @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS)
        private Date publishTime;
        private Integer likeCount;
        private Integer collectCount;
    }
    /**
     * 关键词
     */
    private String searchText;

    /**
     * 作者 id
     */
    private Long userId;

    /**
     * 分类 id
     */
    private Long categoryId;

    private String sortField = ArticleSortFieldEnum.PUBLISH_TIME.getValue();
}
