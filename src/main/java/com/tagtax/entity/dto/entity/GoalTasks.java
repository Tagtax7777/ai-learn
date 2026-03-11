package com.tagtax.entity.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalTasks {
    private Long id;                   // 任务ID
    private Long goalId;              // 关联的目标ID
    private String title;             // 任务标题
    private Integer sortOrder;         // 排序序号
    private Double estimatedHours;  // 预计耗时（小时）
    private Double actualHours;     // 实际耗时（小时）
    private Integer status;            // 0=待办 1=进行中 2=完成 3=跳过
}