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

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.dto.user.UserEditDTO;
import edu.neuq.techhub.domain.dto.user.UserQueryDTO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.UserStatusEnum;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
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

    @Override
    public Integer banUserById(Long id) {
        // 校验用户是否存在
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        UserDO userDO = this.getById(id);
        ThrowUtils.throwIf(userDO == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 校验用户是否已被封禁
        ThrowUtils.throwIf(userDO.getStatus().equals(UserStatusEnum.BAN.getValue()), ErrorCode.OPERATION_ERROR, "用户已被封禁");
        // 封禁用户
        UserDO updateUser = new UserDO();
        updateUser.setId(id);
        updateUser.setStatus(UserStatusEnum.BAN.getValue());
        boolean result = this.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        // 踢人下线
        StpUtil.kickout(id);
        return 0;
    }

    @Override
    public Integer unbanUserById(Long id) {
        // 校验用户是否存在
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        UserDO userDO = this.getById(id);
        ThrowUtils.throwIf(userDO == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 校验用户是否状态正常
        ThrowUtils.throwIf(userDO.getStatus().equals(UserStatusEnum.NORMAL.getValue()), ErrorCode.OPERATION_ERROR, "用户状态正常");
        // 解封用户
        UserDO updateUser = new UserDO();
        updateUser.setId(id);
        updateUser.setStatus(UserStatusEnum.NORMAL.getValue());
        boolean result = this.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        return 0;
    }

    @Override
    public void editById(UserEditDTO userEditDTO, Long id) {
        // 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(userEditDTO.getNickname()), ErrorCode.PARAMS_ERROR, "用户昵称不能为空");
        UserDO userDO = new UserDO();
        BeanUtil.copyProperties(userEditDTO, userDO);
        userDO.setId(id);
        boolean result = this.updateById(userDO);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        // 修改 session 的用户信息
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        BeanUtil.copyProperties(userEditDTO, loginUserVO);
    }
}
