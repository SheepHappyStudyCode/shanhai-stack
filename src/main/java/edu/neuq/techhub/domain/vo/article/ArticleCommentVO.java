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

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.neuq.techhub.domain.entity.ArticleCommentDO;
import edu.neuq.techhub.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "评论视图对象")
public class ArticleCommentVO {

    @Schema(description = "评论主键ID，自增唯一标识")
    private Long id;

    @Schema(description = "关联的文章ID，表明该评论所属的文章")
    private Long articleId;

    // @Schema(description = "关联的文章标题")
    // private String articleTitle;

    @Schema(description = "评论父级id")
    private Long parentId;

    @Schema(description = "评论用户id")
    private Long userId;

    @Schema(description = "发表评论的用户ID")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "回复人 id")
    private Long replyUserId;

    @Schema(description = "回复人昵称")
    private String replyNickname;

    @Schema(description = "被回复的数量")
    private int replyCount = 0;

    @Schema(description = "评论内容，使用utf8mb4字符集以支持更多字符类型")
    private String content;

    @Schema(description = "是否置顶")
    private Integer isStick;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "ip来源")
    private String ipSource;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtils.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @Schema(description = "子评论")
    private List<ArticleCommentVO> children;

    public static ArticleCommentVO obj2vo(ArticleCommentDO articleCommentDO) {
        ArticleCommentVO articleCommentVO = new ArticleCommentVO();
        BeanUtil.copyProperties(articleCommentDO, articleCommentVO);
        return articleCommentVO;
    }
}