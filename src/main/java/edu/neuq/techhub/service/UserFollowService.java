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

package edu.neuq.techhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.neuq.techhub.domain.entity.UserFollowDO;

/**
* @author panda
* @description 针对表【sys_user_follow(用户关注关系表)】的数据库操作Service
* @createDate 2025-03-14 21:36:18
*/
public interface UserFollowService extends IService<UserFollowDO> {

    /**
     *
     * @param follower_id 关注者 id
     * @param following_id 被关注的人的 id
     * @return 0-关注 1-取关
     */
    Integer followUserById(Long follower_id, Long following_id);
}
