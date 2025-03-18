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

package edu.neuq.techhub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.article.tag.ArticleTagAddDTO;
import edu.neuq.techhub.domain.entity.ArticleTagDO;
import edu.neuq.techhub.domain.vo.article.ArticleTagVO;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.ArticleTagMapper;
import edu.neuq.techhub.service.ArticleTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author panda
 * @description 针对表【sys_article_tag(文章标签表)】的数据库操作Service实现
 * @createDate 2025-03-18 14:11:57
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagDO>
        implements
            ArticleTagService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeArticleTagById(Long tagId) {
        // 校验标签是否存在
        ThrowUtils.throwIf(tagId == null || tagId < 0, ErrorCode.PARAMS_ERROR);
        ArticleTagDO articleTagDO = this.getById(tagId);
        ThrowUtils.throwIf(articleTagDO == null, ErrorCode.OPERATION_ERROR, "文章标签不存在");
        if (articleTagDO.getParentId() == null) {
            // 删除所有子标签
            LambdaQueryWrapper<ArticleTagDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ArticleTagDO::getParentId, tagId);
            List<Long> removeIdList = this.list(queryWrapper).stream().map(ArticleTagDO::getId).toList();
            ThrowUtils.throwIf(!removeByIds(removeIdList), ErrorCode.SYSTEM_ERROR);
        }
        ThrowUtils.throwIf(!removeById(tagId), ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public Long addArticleTag(ArticleTagAddDTO articleTagAddDTO) {
        // 名称不能为空
        String name = articleTagAddDTO.getName();
        ThrowUtils.throwIf(StrUtil.isBlank(name), ErrorCode.PARAMS_ERROR, "标签名不能为空");
        // 标签不能重复
        LambdaQueryWrapper<ArticleTagDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTagDO::getName, name);
        Long parentId = articleTagAddDTO.getParentId();
        if (parentId == null) {
            queryWrapper.isNull(ArticleTagDO::getParentId);
        } else {
            queryWrapper.eq(ArticleTagDO::getParentId, parentId);
        }
        ArticleTagDO existTag = getOne(queryWrapper);
        ThrowUtils.throwIf(existTag != null, ErrorCode.OPERATION_ERROR, "文章标签重复");
        if (parentId != null) {
            // 父标签是否存在
            ArticleTagDO parentTag = getById(parentId);
            ThrowUtils.throwIf(parentTag == null, ErrorCode.OPERATION_ERROR, "父标签不存在");
            // 父标签必须是一级标签
            ThrowUtils.throwIf(parentTag.getParentId() != null, ErrorCode.OPERATION_ERROR, "最多支持二级标签");
        }
        // 保存标签
        ArticleTagDO articleTagDO = new ArticleTagDO();
        BeanUtil.copyProperties(articleTagAddDTO, articleTagDO);
        boolean result = save(articleTagDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        return articleTagDO.getId();
    }

    @Override
    public List<ArticleTagVO> getAllTags() {
        List<ArticleTagVO> articleTagVOList = list().stream().map(ArticleTagVO::obj2vo).toList();
        // 获取一级标签
        List<ArticleTagVO> result = articleTagVOList
                .stream()
                .filter(articleTagVO -> articleTagVO.getParentId() == null)
                .sorted(Comparator.comparing(ArticleTagVO::getSort))
                .toList();
        // 将二级标签按照父标签分组
        Map<Long, List<ArticleTagVO>> groupedAndSortedTags = articleTagVOList
                .stream()
                .filter(articleTagVO -> articleTagVO.getParentId() != null)
                .collect(Collectors.groupingBy(
                        ArticleTagVO::getParentId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(ArticleTagVO::getSort))
                                        .collect(Collectors.toList()))));
        // 将二级标签填充到一级标签
        for (ArticleTagVO articleTagVO : result) {
            articleTagVO.setChildren(groupedAndSortedTags.get(articleTagVO.getId()));
        }
        return result;
    }
}
