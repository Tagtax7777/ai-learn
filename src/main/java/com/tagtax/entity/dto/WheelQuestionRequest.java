package com.tagtax.entity.dto;

import lombok.Data;
import java.util.ArrayList;

/**
 * 大转盘 AI 生成题目传输对象 (DTO)
 * 用于在后端与 AI 接口交互，或向前端下发动态生成的题目数据
 */
@Data
public class WheelQuestionRequest {

    /**
     * 难度等级
     * 对应转盘上抽中的数字，范围通常为 1-9。数字越大，AI 生成的题目越难。
     */
    private Integer difficultyLevel;

    /**
     * 题目类型
     * 约定规范: 1-单选题, 2-判断题, 3-填空题
     */
    private Integer questionType;

    /**
     * 题目正文 / 题干
     * 由 AI 根据用户当前的学习目标 (learning_goals) 实时生成的内容
     */
    private String questionText;

    /**
     * 题目选项列表
     * - 单选题: 存放具体选项，如 ["A. 苹果", "B. 香蕉", "C. 橘子", "D. 葡萄"]
     * - 判断题: 存放对错选项，如 ["正确", "错误"]
     * - 填空题: 此处通常为空 (null 或 empty list)
     */
    private ArrayList<String> options;

    /**
     * 正确答案
     * 供前端拿到后，直接在用户本地设备上与用户的输入进行严格比对 (===)
     */
    private String correctAnswer;

}