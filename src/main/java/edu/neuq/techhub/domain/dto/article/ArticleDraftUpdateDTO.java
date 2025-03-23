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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章表
 * @TableName sys_article
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDraftUpdateDTO {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章简介
     */
    private String summary;

    /**
     * 文章封面地址
     */
    private String cover;

    /**
     * 文章内容html格式
     */
    private String contentHtml;

    /**
     * 文章内容md格式
     */
    private String contentMd;

    /**
     * 分类 id
     */
    private Long categoryId;

    /**
     * 文章标签 json
     */
    private List<String> tagList;

    /**
     * 预估阅读时间(分钟)
     */
    private Integer readTime;

    /**
     * 是否原创  0：转载 1:原创
     */
    private Integer isOriginal;

    /**
     * 转载地址
     */
    private String originalUrl;
}