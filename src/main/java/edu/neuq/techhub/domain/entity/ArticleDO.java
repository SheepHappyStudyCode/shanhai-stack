package edu.neuq.techhub.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 文章表
 * @TableName sys_article
 */
@TableName(value ="sys_article")
@Data
public class ArticleDO extends  BaseDO{
    /**
     * 主键 id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    private String tags;

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
}