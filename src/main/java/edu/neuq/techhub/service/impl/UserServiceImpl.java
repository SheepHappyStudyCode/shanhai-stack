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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.user.UserQueryDTO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.exception.BusinessException;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author panda
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2025-03-13 20:50:50
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
        implements
            UserService {

    @Override
    public Page<UserDO> pageQueryUsers(UserQueryDTO userQueryDTO) {
        ThrowUtils.throwIf(userQueryDTO == null, ErrorCode.PARAMS_ERROR, "参数为空");
        int current = userQueryDTO.getCurrent();
        int size = userQueryDTO.getSize();

        if (current < 1 || current > 20 || size < 1 || size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页参数不合法");
        }
        String username = userQueryDTO.getUsername();
        String phone = userQueryDTO.getPhone();
        String mail = userQueryDTO.getMail();
        String nickname = userQueryDTO.getNickname();
        String profile = userQueryDTO.getProfile();
        String role = userQueryDTO.getRole();
        Integer status = userQueryDTO.getStatus();
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(username), UserDO::getUsername, username);
        queryWrapper.eq(StrUtil.isNotBlank(phone), UserDO::getPhone, phone);
        queryWrapper.eq(StrUtil.isNotBlank(mail), UserDO::getMail, mail);
        queryWrapper.like(StrUtil.isNotBlank(nickname), UserDO::getNickname, nickname);
        queryWrapper.like(StrUtil.isNotBlank(profile), UserDO::getProfile, profile);
        queryWrapper.eq(StrUtil.isNotBlank(role), UserDO::getRole, role);
        queryWrapper.eq(status != null, UserDO::getStatus, status);

        return this.page(userQueryDTO.toMpPageDefaultSortByCreateTimeDesc(), queryWrapper);
    }
}
