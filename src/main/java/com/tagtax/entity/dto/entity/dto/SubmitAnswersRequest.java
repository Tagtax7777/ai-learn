package com.tagtax.entity.dto.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswersRequest {
    private Long levelId;               // 关卡ID
    private List<String> userAnswers;   // 用户答案列表
}