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
public class Level {
    private Long id;                   // 主键ID
    private String levelName;          // 关卡名称
    private Integer levelNumber;       // 关卡序号
    private String description;        // 关卡描述
    private String requiredGoals;      // 关联的学习目标ID(JSON格式)
    private String unlockCondition;    // 解锁条件
    private Integer maxStars;          // 最高星级
    private Integer isActive;          // 是否启用(1启用 0禁用)
    private LocalDateTime createdAt;   // 创建时间
    private Integer starsEarned;       // 用户已获得的星星数
}