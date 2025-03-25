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

import edu.neuq.techhub.aop.ratelimiter.RateLimiter;
import edu.neuq.techhub.common.BaseResponse;
import edu.neuq.techhub.common.ResultUtils;
import edu.neuq.techhub.domain.enums.ImageTypeEnum;
import edu.neuq.techhub.exception.BusinessException;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "文件模块", description = "用来上传文件")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload-image")
    @Operation(summary = "上传图片")
    @RateLimiter(key = "#{T(cn.dev33.satoken.stp.StpUtil).getLoginIdAsString()}", time = 60, count = 10)
    public BaseResponse<String> uploadImage(MultipartFile file, Integer type) {
        ThrowUtils.throwIf(file == null || type == null, ErrorCode.PARAMS_ERROR);
        String typeStr = Optional.ofNullable(ImageTypeEnum.getEnumByValue(type)).map(ImageTypeEnum::getText).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "图片类型不合法"));
        String path = "image/" + DateUtils.parseDateToStr(DateUtils.YYYYMMDD, DateUtils.getNowDate()) + "/" + typeStr + "/";
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(path)
                .image(type.equals(ImageTypeEnum.AVATAR.getValue()), img -> img.size(150, 150))
                .upload();
        ThrowUtils.throwIf(fileInfo == null, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(fileInfo.getUrl());
    }

}