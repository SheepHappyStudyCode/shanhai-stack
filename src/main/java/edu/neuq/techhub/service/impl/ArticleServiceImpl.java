package edu.neuq.techhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.entity.ArticleDO;
import edu.neuq.techhub.service.ArticleService;
import edu.neuq.techhub.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

/**
* @author panda
* @description 针对表【sys_article(文章表)】的数据库操作Service实现
* @createDate 2025-03-16 12:34:59
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleDO>
    implements ArticleService{

}




