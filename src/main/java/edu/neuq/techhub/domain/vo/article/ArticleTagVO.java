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

package edu.neuq.techhub.domain.vo.article;

import edu.neuq.techhub.domain.entity.ArticleTagDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class ArticleTagVO {

    /**
     * 标签 ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    private Long parentId;

    /**
     * 子标签列表
     */
    private List<ArticleTagVO> children;

    public static ArticleTagVO obj2vo(ArticleTagDO articleTagDO) {
        ArticleTagVO articleTagVO = new ArticleTagVO();
        BeanUtils.copyProperties(articleTagDO, articleTagVO);
        return articleTagVO;
    }
}