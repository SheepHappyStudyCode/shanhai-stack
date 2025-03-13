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

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.neuq.techhub.domain.dto.user.UserLoginDTO;
import edu.neuq.techhub.domain.dto.user.UserRegisterDTO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.enums.UserRoleEnum;
import edu.neuq.techhub.domain.enums.UserStatusEnum;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.exception.BusinessException;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    @Override
    public LoginUserVO userRegister(UserRegisterDTO userRegisterDto) {
        // 校验参数
        validateUserRegisterDto(userRegisterDto);
        // 用户名是否存在
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, userRegisterDto.getUsername());
        UserDO userDO = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(userDO != null, ErrorCode.PARAMS_ERROR, "用户名重复");
        // 构造用户数据
        userDO = new UserDO();
        userDO.setUsername(userRegisterDto.getUsername());
        userDO.setPassword(BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt()));
        userDO.setNickname("普通用户-" + RandomUtil.randomString(10));
        userDO.setRole(UserRoleEnum.USER.getValue());
        userDO.setStatus(UserStatusEnum.NORMAL.getValue());
        userDO.setEditTime(new Date());
        userDO.setCreateTime(new Date());
        int insert = userMapper.insert(userDO);
        ThrowUtils.throwIf(insert != 1, ErrorCode.SYSTEM_ERROR, "用户名重复");
        // 保存登录态
        StpUtil.login(userDO.getId());
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(userDO, loginUserVO);
        StpUtil.getSession().set("user", loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLoginByPassword(UserLoginDTO userLoginDTO) {
        // 校验参数
        ThrowUtils.throwIf(userLoginDTO == null, ErrorCode.PARAMS_ERROR, "参数为空");
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        ThrowUtils.throwIf(StrUtil.hasBlank(username, password), ErrorCode.PARAMS_ERROR, "参数为空");
        // 用户是否存在
        LambdaQueryWrapper<UserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDO::getUsername, username);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(userDO == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        // 密码是否正确
        boolean checkpw = BCrypt.checkpw(password, userDO.getPassword());
        ThrowUtils.throwIf(!checkpw, ErrorCode.PARAMS_ERROR, "密码不正确");
        // 保存登录态
        StpUtil.login(userDO.getId());
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(userDO, loginUserVO);
        StpUtil.getSession().set("user", loginUserVO);
        return loginUserVO;
    }

    void validateUserRegisterDto(UserRegisterDTO userRegisterDto) {
        ThrowUtils.throwIf(userRegisterDto == null, ErrorCode.PARAMS_ERROR, "参数为空");
        String username = userRegisterDto.getUsername();
        String password = userRegisterDto.getPassword();
        // 参数不能为空
        if (StrUtil.hasBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 校验长度
        if (username.length() < 4 || username.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度必须 4~16");
        }

        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码至少 8 位");
        }
        // 用户名必须以字母开头
        ThrowUtils.throwIf(!Character.isLetter(username.charAt(0)), ErrorCode.PARAMS_ERROR, "用户名必须以字母开头");
        // 用户名只能包含数字和字母
        ThrowUtils.throwIf(!username.matches("^[a-zA-Z][a-zA-Z0-9]*$"), ErrorCode.PARAMS_ERROR, "用户名只能包含数字和字母");
    }
}
