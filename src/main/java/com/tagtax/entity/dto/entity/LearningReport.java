package com.tagtax.entity.dto.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningReport {
    private Long id;                    // 主键ID
    private Long userId;                // 用户ID
    private LocalDate reportDate;       // 报告日期
    private LearningAnalysis learningAnalysis; // 学习分析数据
    private LocalDateTime createdAt;    // 创建时间
    
    /**
     * 学习分析数据内部类
     * 对应JSON格式的学习分析数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LearningAnalysis {
        private List<Integer> dayData;    // 一天内各时段学习数据(7:00-18:00)，范围0-100
        private List<Integer> weekData;   // 一周内各天学习数据(周一至周日)，范围0-100
        private List<Integer> monthData;  // 一年内各月学习数据(1-12月)，范围0-100
        private List<Double> personData;  // 个人学习效率数据(周一至周日)，范围0-4
        private List<Double> crowdData;   // 群体学习效率数据(周一至周日)，范围0-4
        private String summary;           // AI生成的学习小结，不超过30个字
    }
}