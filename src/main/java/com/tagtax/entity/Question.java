package com.tagtax.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private Long id;                    // 主键ID
    private String questionText;        // 题目内容
    private String optionA;            // 选项A
    private String optionB;            // 选项B
    private String optionC;            // 选项C
    private String optionD;            // 选项D
    private String correctAnswer;      // 正确答案(A/B/C/D)
    private Integer difficultyLevel;   // 难度等级(1-5)
    private String knowledgePoint;     // 知识点标签
    private String explanation;        // 答案解析
    private LocalDateTime createdAt;   // 创建时间
}