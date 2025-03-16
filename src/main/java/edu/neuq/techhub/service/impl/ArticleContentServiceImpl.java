package edu.neuq.techhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.entity.ArticleContentDO;
import edu.neuq.techhub.service.ArticleContentService;
import edu.neuq.techhub.mapper.ArticleContentMapper;
import org.springframework.stereotype.Service;

/**
* @author panda
* @description 针对表【sys_article_content(文章内容表)】的数据库操作Service实现
* @createDate 2025-03-16 12:49:29
*/
@Service
public class ArticleContentServiceImpl extends ServiceImpl<ArticleContentMapper, ArticleContentDO>
    implements ArticleContentService{

}




