package com.tagtax.service;

import com.tagtax.entity.Result;

public interface AIReportService {
    
    /**
     * 获取或生成用户最新AI学习报告
     * @param userId 用户ID
     * @return 操作结果
     */
    Result getLatestAIReport(Long userId);
}