package com.tagtax.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalTasks {
    private Long id;                   // 任务ID
    private Long goalId;              // 关联的目标ID
    private String title;             // 任务标题
    private Integer taskType;          // 1=学习 2=练习 3=复习 4=测试
    private Integer sortOrder;         // 排序序号
    private Double estimatedHours;  // 预计耗时（小时）
    private Double actualHours;     // 实际耗时（小时）
    private Integer status;            // 0=待办 1=进行中 2=完成 3=跳过
}