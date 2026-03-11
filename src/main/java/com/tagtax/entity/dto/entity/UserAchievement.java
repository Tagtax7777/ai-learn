package com.tagtax.entity.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {
    private Long id;                    // 记录ID
    private Long userId;                // 用户ID
    private Integer achievementId;      // 成就ID
    private LocalDateTime unlockedAt;    // 解锁时间
    private String unlockSource;        // 解锁来源
    
    // 可选：关联的成就详情（非数据库字段，用于业务处理）
    private Achievement achievementDetail;
}