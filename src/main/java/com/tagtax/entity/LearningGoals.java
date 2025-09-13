package com.tagtax.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningGoals {
    private Long id;                   // 对应数据库主键
    private Long userId;               // 用户ID
    private String title;              // 目标标题
    private Integer goalType;          // 1=考试 2=技能 3=课程 4=自定义
    private LocalDate endDate;         // 目标结束日期
    private LocalDate startDate;       // 开始日期
    private Integer status;            // 0=进行中 1=已完成 2=已暂停 3=已取消
    private LocalDateTime completionDate; // 完成时间
    private Double estimatedHours;     // 预计总耗时(小时)
    private Double actualHours;        // 实际总耗时(小时)

    private List<GoalTasks> goalTasks; // 非数据库字段，用来进行业务处理
}