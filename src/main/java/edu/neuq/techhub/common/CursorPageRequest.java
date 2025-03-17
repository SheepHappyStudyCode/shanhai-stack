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

package edu.neuq.techhub.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CursorPageRequest<C> {

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    // 游标值，可以是ID或时间戳等
    private C cursor;

    // 每页大小
    private int size = DEFAULT_PAGE_SIZE;

    // 是否向前查询（即向下一页查询）
    private boolean forward = true;

    // 是否升序
    private boolean isAsc = false;

    /**
     * 检查是否是第一次查询(没有游标值)
     */
    public boolean isFirstQuery() {
        return cursor == null;
    }
}