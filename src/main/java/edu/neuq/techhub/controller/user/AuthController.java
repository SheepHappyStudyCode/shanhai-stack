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

package edu.neuq.techhub.controller.user;

import cn.dev33.satoken.stp.StpUtil;
import edu.neuq.techhub.aop.ratelimiter.LimitType;
import edu.neuq.techhub.aop.ratelimiter.RateLimiter;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.user.UserLoginDTO;
import edu.neuq.techhub.domain.dto.user.UserRegisterDTO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "权限模块", description = "包含用户注册、登录")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public BaseResponse<LoginUserVO> userRegister(@RequestBody UserRegisterDTO userRegisterDto) {
        LoginUserVO loginUserVO = authService.userRegister(userRegisterDto);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login/password")
    @Operation(summary = "使用密码登录")
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    public BaseResponse<LoginUserVO> loginByPassword(@RequestBody UserLoginDTO userLoginDTO) {
        LoginUserVO loginUserVO = authService.userLoginByPassword(userLoginDTO, false);
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public BaseResponse<Integer> userLogout() {
        StpUtil.logout();
        return ResultUtils.success(0);
    }

}