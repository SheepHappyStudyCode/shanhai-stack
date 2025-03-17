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

import java.util.List;

@Data
@Accessors(chain = true)
public class CursorPageResult<T, C> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 是否还有更多数据
     */
    private boolean hasMore;

    /**
     * 下一页的游标值
     */
    private C nextCursor;

    /**
     * 上一页的游标值
     */
    private C prevCursor;

    /**
     * 创建一个空的结果对象
     */
    public static <T, C> CursorPageResult<T, C> empty() {
        return new CursorPageResult<T, C>()
                .setHasMore(false)
                .setNextCursor(null)
                .setPrevCursor(null);
    }

    /**
     * 从数据列表创建结果对象
     * @param records 数据列表
     * @param hasMore 是否还有更多数据
     * @param nextCursor 下一页游标
     */
    public static <T, C> CursorPageResult<T, C> of(List<T> records, boolean hasMore, C nextCursor) {
        return CursorPageResult.of(records, hasMore, nextCursor, null);
    }

    /**
     * 从数据列表创建结果对象
     * @param records 数据列表
     * @param hasMore 是否还有更多数据
     * @param nextCursor 下一页游标
     * @param prevCursor 上一页游标
     */
    public static <T, C> CursorPageResult<T, C> of(List<T> records, boolean hasMore, C nextCursor, C prevCursor) {
        return new CursorPageResult<T, C>()
                .setRecords(records)
                .setHasMore(hasMore)
                .setNextCursor(nextCursor)
                .setPrevCursor(prevCursor);
    }

    /**
     * 获取当前页的大小
     */
    public int getSize() {
        return records == null ? 0 : records.size();
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return records == null || records.isEmpty();
    }
}