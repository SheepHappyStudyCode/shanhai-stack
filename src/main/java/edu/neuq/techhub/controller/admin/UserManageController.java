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

package edu.neuq.techhub.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.dto.user.UserQueryDTO;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserService userService;

    @Operation(description = "分页查询用户")
    @GetMapping
    public BaseResponse<Page<UserDO>> pageQueryUsers(@ParameterObject UserQueryDTO userQueryDTO) {
        Page<UserDO> userDOPage = userService.pageQueryUsers(userQueryDTO);
        return ResultUtils.success(userDOPage);
    }

    @Operation(description = "封禁用户")
    @PostMapping("{id}/ban")
    public BaseResponse<Integer> banUser(@PathVariable Long id) {
        Integer res = userService.banUserById(id);
        return ResultUtils.success(res);
    }

    @Operation(description = "解封用户")
    @PostMapping("{id}/unban")
    public BaseResponse<Integer> unbanUser(@PathVariable Long id) {
        Integer res = userService.unbanUserById(id);
        return ResultUtils.success(res);
    }

}