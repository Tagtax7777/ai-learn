package com.tagtax.service;

import com.tagtax.entity.Result;

public interface LearningReportService {
    
    /**
     * 获取或生成用户最新学习报告
     * @param userId 用户ID
     * @return 操作结果
     */
    Result getLatestLearningReport(Long userId);
}