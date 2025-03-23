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

package edu.neuq.techhub.manager;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import edu.neuq.techhub.domain.dto.article.ArticleDraftUpdateDTO;
import edu.neuq.techhub.exception.BusinessException;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlManager {

    private final ArticleService articleService;

    @Transactional
    public ArticleDraftUpdateDTO crawlArticle(String url, Long userId) {
        try {
            Document document = Jsoup.connect(url).get();
            org.jsoup.select.Elements title = document.getElementsByClass("title-article");
            org.jsoup.select.Elements tags = document.getElementsByClass("tag-link");
            Elements content = document.getElementsByClass("article_content");
            if (StringUtils.isBlank(content.toString())) {
                throw new BusinessException(ErrorCode.CRAWL_ERROR, "爬取内容为空");
            }

            // 爬取的是HTML内容，需要转成MD格式的内容
            String contentHtml = content.get(0).toString().replaceAll("<code>", "<code class=\"lang-java\">");
            String markdown = FlexmarkHtmlConverter.builder(new MutableDataSet()).build().convert(contentHtml)
                    .replace("lang-java", "java");
            // 文章标签
            List<String> tagList = tags.stream().map(Element::text).toList();
            ArticleDraftUpdateDTO articleDraftUpdateDTO = ArticleDraftUpdateDTO.builder().title(title.get(0).text()).contentMd(markdown).contentHtml(contentHtml)
                    .isOriginal(0).originalUrl(url)
                    .cover("https://api.btstu.cn/sjbz/api.php?lx=dongman&format=images")
                    .tagList(tagList)
                    .build();

            Long draftId = articleService.createDraft(userId);
            articleDraftUpdateDTO.setId(draftId);
            articleService.saveDraft(articleDraftUpdateDTO, userId);
            return articleDraftUpdateDTO;
        } catch (IOException e) {
            log.error("文章爬取失败，url = {}", url);
            throw new BusinessException(ErrorCode.CRAWL_ERROR);
        }

    }
}
