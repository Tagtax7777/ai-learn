package com.tagtax.service;

import com.tagtax.entity.Result;

import java.util.List;

public interface LevelService {
    
    /**
     * 获取用户可用关卡列表
     * @param userId 用户ID
     * @return 关卡列表
     */
    Result getUserAvailableLevels(Long userId);
    
    /**
     * 生成关卡题目
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @param goalIds 学习目标ID列表
     * @return 题目列表
     */
    Result generateLevelQuestions(Long userId, Long levelId, List<Long> goalIds);
    
    /**
     * 评估用户答案
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @param userAnswers 用户答案列表
     * @return 评估结果
     */
    Result evaluateUserAnswers(Long userId, Long levelId, List<String> userAnswers);
    
    /**
     * 获取用户关卡历史记录
     * @param userId 用户ID
     * @param levelId 关卡ID
     * @return 历史记录
     */
    Result getUserLevelHistory(Long userId, Long levelId);
    
    /**
     * 获取所有关卡信息
     * @return 所有关卡
     */
    Result getAllLevels();
    
    /**
     * 获取用户每日挑战统计
     * @param userId 用户ID
     * @return 每日统计信息
     */
    Result getUserDailyStats(Long userId);
}