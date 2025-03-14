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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neuq.techhub.domain.entity.UserDO;
import edu.neuq.techhub.domain.entity.UserFollowDO;
import edu.neuq.techhub.domain.entity.UserStatsDO;
import edu.neuq.techhub.domain.enums.UserFollowEnum;
import edu.neuq.techhub.exception.ErrorCode;
import edu.neuq.techhub.exception.ThrowUtils;
import edu.neuq.techhub.mapper.UserFollowMapper;
import edu.neuq.techhub.mapper.UserMapper;
import edu.neuq.techhub.mapper.UserStatsMapper;
import edu.neuq.techhub.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author panda
* @description 针对表【sys_user_follow(用户关注关系表)】的数据库操作Service实现
* @createDate 2025-03-14 21:36:18
*/
@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollowDO>
        implements
            UserFollowService {

    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;;
    private final UserStatsMapper userStatsMapper;;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer followUserById(Long follower_id, Long following_id) {
        // 关注对象是否存在
        ThrowUtils.throwIf(following_id == null || following_id <= 0, ErrorCode.PARAMS_ERROR);
        UserDO following_user = userMapper.selectById(following_id);
        ThrowUtils.throwIf(following_user == null, ErrorCode.PARAMS_ERROR, "关注的用户不存在");
        // 关注关系是否存在
        LambdaQueryWrapper<UserFollowDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollowDO::getFollowerId, follower_id);
        queryWrapper.eq(UserFollowDO::getFollowingId, following_id);
        UserFollowDO userFollowDO = userFollowMapper.selectOne(queryWrapper);
        // 如果关注关系不存在
        if (userFollowDO == null) {
            // 创建关注关系
            userFollowDO = new UserFollowDO();
            userFollowDO.setFollowerId(follower_id);
            userFollowDO.setFollowingId(following_id);
            userFollowDO.setStatus(UserFollowEnum.FOLLOW.getValue());
            int insert = userFollowMapper.insert(userFollowDO);
            ThrowUtils.throwIf(insert == 0, ErrorCode.SYSTEM_ERROR);
            // 本人关注数 +1
            LambdaUpdateWrapper<UserStatsDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.setIncrBy(UserStatsDO::getFollowingCount, 1);
            updateWrapper.eq(UserStatsDO::getId, follower_id);
            int update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            // 关注着的被关注数 +1
            updateWrapper.clear();
            updateWrapper.setIncrBy(UserStatsDO::getFollowerCount, 1);
            updateWrapper.eq(UserStatsDO::getId, following_id);
            update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            return UserFollowEnum.FOLLOW.getValue();
        }
        int result;
        // 如果关注关系存在
        if (userFollowDO.getStatus().equals(UserFollowEnum.FOLLOW.getValue())) {
            // 取关
            UserFollowDO updateFollow = new UserFollowDO();
            updateFollow.setId(userFollowDO.getId());
            updateFollow.setStatus(UserFollowEnum.UNFOLLOW.getValue());
            int update = userFollowMapper.updateById(updateFollow);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            // 关注数 -1
            LambdaUpdateWrapper<UserStatsDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.setDecrBy(UserStatsDO::getFollowingCount, 1);
            updateWrapper.eq(UserStatsDO::getId, follower_id);
            update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            // 被关注数 -1
            updateWrapper.clear();
            updateWrapper.setDecrBy(UserStatsDO::getFollowerCount, 1);
            updateWrapper.eq(UserStatsDO::getId, following_id);
            update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            result = UserFollowEnum.UNFOLLOW.getValue();
        } else {
            // 关注
            UserFollowDO updateFollow = new UserFollowDO();
            updateFollow.setId(userFollowDO.getId());
            updateFollow.setStatus(UserFollowEnum.FOLLOW.getValue());
            int update = userFollowMapper.updateById(updateFollow);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            // 关注数 +1
            LambdaUpdateWrapper<UserStatsDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.setIncrBy(UserStatsDO::getFollowingCount, 1);
            updateWrapper.eq(UserStatsDO::getId, follower_id);
            update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            // 被关注数 +1
            updateWrapper.clear();
            updateWrapper.setIncrBy(UserStatsDO::getFollowerCount, 1);
            updateWrapper.eq(UserStatsDO::getId, following_id);
            update = userStatsMapper.update(updateWrapper);
            ThrowUtils.throwIf(update != 1, ErrorCode.SYSTEM_ERROR);
            result = UserFollowEnum.FOLLOW.getValue();
        }
        return result;
    }
}
