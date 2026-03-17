package com.tagtax.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WheelMapper {

    Integer addRewards(@Param("userId")Long userId,
                       @Param("rewardType")Integer rewardType,
                       @Param("rewardName")String rewardName);
}
