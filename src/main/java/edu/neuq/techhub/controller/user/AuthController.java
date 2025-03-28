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

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.IdUtil;
import edu.neuq.techhub.aop.ratelimiter.LimitType;
import edu.neuq.techhub.aop.ratelimiter.RateLimiter;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.constant.GlobalConstant;
import edu.neuq.techhub.domain.dto.user.UserLoginDTO;
import edu.neuq.techhub.domain.dto.user.UserRegisterDTO;
import edu.neuq.techhub.domain.vo.CaptchaVO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.service.AuthService;
import edu.neuq.techhub.utils.RedisUtils;
import edu.neuq.techhub.utils.SpringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

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

    @GetMapping("/captcha")
    @Operation(summary = "获取密码登录的验证码")
    @SaIgnore
    @RateLimiter(time = 60, count = 3, limitType = LimitType.IP)
    public BaseResponse<CaptchaVO> getCaptcha() {
        // 生成验证码
        CodeGenerator codeGenerator = new RandomGenerator("0123456789", 4);
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100);
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = GlobalConstant.CAPTCHA_CODE_KEY + uuid;
        // 图片的答案
        String code = captcha.getCode();
        // 放入缓存
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(GlobalConstant.CAPTCHA_EXPIRATION));

        CaptchaVO captchaVo = new CaptchaVO();
        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return ResultUtils.success(captchaVo);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public BaseResponse<Integer> userLogout() {
        StpUtil.logout();
        return ResultUtils.success(0);
    }

}