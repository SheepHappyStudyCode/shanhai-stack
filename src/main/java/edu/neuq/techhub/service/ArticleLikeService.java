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

package edu.neuq.techhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neuq.techhub.domain.entity.ArticleLikeDO;

/**
* @author panda
* @description 针对表【sys_article_like(文章点赞表)】的数据库操作Service
* @createDate 2025-03-18 15:32:21
*/
public interface ArticleLikeService extends IService<ArticleLikeDO> {

    Integer likeArticle(Long userId, Long articleId);
}
