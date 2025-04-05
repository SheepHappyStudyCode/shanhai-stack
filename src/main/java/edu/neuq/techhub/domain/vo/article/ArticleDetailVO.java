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

import edu.neuq.techhub.domain.vo.user.UserVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArticleDetailVO {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 作者 id
     */
    private Long userId;

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
     * 分类 id
     */
    private Long categoryId;

    /**
     * 文章标签 json
     */
    private List<String> tagList;

    /**
     * 文章内容html格式
     */
    private String contentHtml;

    /**
     * 文章内容md格式
     */
    private String contentMd;

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

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 文章状态 0-草稿 1-已发布  2-审核通过 3-审核不通过 4-已下架
     */
    private Integer status;

    /**
     * 审核人 ID
     */
    private Long reviewerId;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    private UserVO userVO;

    private String categoryName;

    private boolean isLiked = false;

    private boolean isCollected = false;


}
