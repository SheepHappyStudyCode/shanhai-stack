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

package edu.neuq.techhub.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文章评论
 * @TableName sys_article_comment
 */
@TableName(value = "sys_article_comment")
@Data
public class ArticleCommentDO extends BaseDO1 {

    /**
     * 文章评论主键ID，自增唯一标识
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联的文章ID，表明该评论所属的文章
     */
    private Long articleId;

    /**
     * 发表评论的用户ID
     */
    private Long userId;

    /**
     * 回复人id
     */
    private Long replyUserId;

    /**
     * 父评论ID，用于实现回复评论的层级结构，若为顶级评论则为NULL
     */
    private Long parentId;

    /**
     * 评论内容，使用utf8mb4字符集以支持更多字符类型
     */
    private String content;

    /**
     * 点赞数，记录该评论获得的点赞数量
     */
    private Integer likeCount;

    /**
     * 是否置顶
     */
    private Integer isStick;

    /**
     * ip
     */
    private String ip;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * ip来源
     */
    private String ipSource;
}