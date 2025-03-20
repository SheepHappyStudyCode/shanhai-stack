package edu.neuq.techhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.entity.ArticleCommentDO;
import edu.neuq.techhub.service.ArticleCommentService;
import edu.neuq.techhub.mapper.ArticleCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author panda
* @description 针对表【sys_article_comment(文章评论)】的数据库操作Service实现
* @createDate 2025-03-20 23:35:12
*/
@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleCommentDO>
    implements ArticleCommentService{

}




