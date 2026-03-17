package com.tagtax.entity.dto;

import lombok.Data;

/**
 * 大转盘获奖请求 (DTO)
 */
@Data
public class RewardRequest {

    private  Integer rewardType;

    private String rewardName;

}
