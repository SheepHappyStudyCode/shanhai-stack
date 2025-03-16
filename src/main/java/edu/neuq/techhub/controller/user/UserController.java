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
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.user.UserEditDTO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.vo.user.LoginUserVO;
import edu.neuq.techhub.domain.vo.user.UserVO;
import edu.neuq.techhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户模块", description = "用户的查询、编辑等")
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    @Operation(summary = "查询我的信息")
    public BaseResponse<LoginUserVO> getMyInfo() {
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        return ResultUtils.success(loginUserVO);
    }


    @GetMapping("/{id}")
    @SaIgnore
    @Operation(summary = "根据 id 查询用户")
    public BaseResponse<UserVO> getUserVOById(@PathVariable Long id) {
        UserDO userDO = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);
        return ResultUtils.success(userVO);
    }


    @PatchMapping("/me")
    @Operation(summary = "编辑个人信息")
    public BaseResponse<Integer> editMe(@RequestBody UserEditDTO userEditDTO) {
        LoginUserVO loginUserVO = (LoginUserVO) StpUtil.getSession().get("user");
        userService.editById(userEditDTO, loginUserVO.getId());
        return ResultUtils.success(0);
    }

}