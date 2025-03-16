package edu.neuq.techhub.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 文章内容表
 * @TableName sys_article_content
 */
@TableName(value ="sys_article_content")
@Data
public class ArticleContentDO extends BaseDO{
    /**
     * 文章id
     */
    @TableId
    private Long id;

    /**
     * 文章内容html格式
     */
    private String contentHtml;

    /**
     * 文章内容md格式
     */
    private String contentMd;
}