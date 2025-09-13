package com.tagtax.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecord {
    private Long id;                    // 主键ID
    private Long userId;                // 用户ID
    private LocalTime startTime;        // 学习开始时间(如12:52)
    private LocalTime endTime;          // 学习结束时间(如12:53)
    private LocalDate studyDate;        // 学习日期(如2025-09-13)
    private LocalDateTime createdAt;    // 记录创建时间
}