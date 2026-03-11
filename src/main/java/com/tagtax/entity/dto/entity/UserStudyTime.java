package com.tagtax.entity.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStudyTime {
    private Long id;
    private Long userId;
    private Double totalHours;
    private Double dailyHours;
    private Integer points;
    private LocalDate lastStudyDate;
    private LocalDateTime updateTime;
}