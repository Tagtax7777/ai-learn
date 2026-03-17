package com.tagtax.service;


import com.tagtax.entity.Result;

public interface WheelService {
    Result getQuestion(Long id, Integer difficultyLevel);

    Result claimReward(Integer difficultyLevel, Long id);
}
