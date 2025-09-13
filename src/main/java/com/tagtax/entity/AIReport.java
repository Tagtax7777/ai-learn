package com.tagtax.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AIReport {
    private Long id;                    // 主键ID
    private Long userId;                // 用户ID
    private LocalDate reportDate;       // 报告日期
    private Report learningAnalysis;    // 学习分析数据
    private LocalDateTime createdAt;    // 创建时间
    
    /**
     * 学习分析报告
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Report {
        private FocusAnalysis focusAnalysis;           // 专注力分析
        private KnowledgeMastery knowledgeMastery;     // 知识掌握度
        private TimeDistribution timeDistribution;     // 时间分布
        private DifficultyDistribution difficultyDistribution; // 难度分布
        private RecommendedLearning recommendedLearning; // 学习建议
        private String aiSuggestion;                   // AI总结建议
    }
    
    /**
     * 专注力分析
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FocusAnalysis {
        private List<FocusItem> data;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class FocusItem {
            private String grade;        // 评级
            private String evaluation;   // 评价
            private String timePeriod;   // 时间段
        }
    }
    
    /**
     * 知识掌握度
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KnowledgeMastery {
        private List<KnowledgeItem> data;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class KnowledgeItem {
            private String title;        // 知识点名称
            private Integer percentage;   // 掌握百分比
        }
    }
    
    /**
     * 时间分布
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeDistribution {
        private List<TimeItem> data;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class TimeItem {
            private String name;         // 知识点名称
            private Integer value;       // 时间值
        }
    }
    
    /**
     * 难度分布
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DifficultyDistribution {
        private List<DifficultyItem> data;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DifficultyItem {
            private String name;         // 知识点名称
            private Integer level;       // 难度级别
        }
    }
    
    /**
     * 推荐学习
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendedLearning {
        private List<RecommendItem> data;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RecommendItem {
            private String name;         // 学习项目名称
            private Integer time;        // 建议时间
            private String remainder;    // 备注
        }
    }
}