package com.tagtax.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Achievement {
    private Integer id;                 // 成就ID
    private String name;               // 成就名称
    private String description;        // 成就描述
    private String category;           // 成就分类
    private String iconUrl;            // 成就图标URL
    private Integer pointsReward;      // 积分奖励
    private Integer rarityLevel;       // 稀有度(1-4)
    private String unlockCondition;    // 解锁条件描述
    private Integer conditionType;     // 条件类型(1-4)
    private Boolean isHidden;          // 是否隐藏成就
    private Boolean isActive;          // 是否启用
    private Integer sortOrder;         // 排序权重
    private LocalDateTime createdAt;   // 创建时间
}