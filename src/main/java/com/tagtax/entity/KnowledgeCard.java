package com.tagtax.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeCard {
    private Long id;                    // 主键ID
    private Long taskId;                // 关联的子任务ID
    private String cardName;            // 知识卡片名称（如：场景记忆法）
    private String explanation;         // 知识解释
    private String example;             // 举例说明
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
}
