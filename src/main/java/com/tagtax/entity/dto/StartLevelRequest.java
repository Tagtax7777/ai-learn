package com.tagtax.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class StartLevelRequest {
    private Long levelId;           // 关卡ID
    private List<Long> goalIds;     // 学习目标ID列表
}