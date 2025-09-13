package com.tagtax.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLevelRecord {
    private Long id;                   // 主键ID
    private Long userId;               // 用户ID
    private Long levelId;              // 关卡ID
    private Integer starsEarned;       // 获得星级(1-3)
    private Integer score;             // 得分
    private Integer totalQuestions;    // 总题数
    private Integer correctAnswers;    // 正确答案数
    private Integer completionTime;    // 完成时间(秒)
    private String qualityAnalysis;    // AI分析结果(JSON格式)
    private LocalDateTime completedAt; // 完成时间
}