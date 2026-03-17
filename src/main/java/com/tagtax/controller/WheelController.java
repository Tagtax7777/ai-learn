package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.service.WheelService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 抽奖转盘接口
 */
@RestController
@RequestMapping("/api/wheel")
public class WheelController {

    @Autowired
    private WheelService wheelService;

    /**
     * 获取题目接口
     */
    @GetMapping("/question")
    public Result getQuestion(
            @RequestHeader("Authorization")  String token,
            @RequestParam("difficultyLevel") Integer difficultyLevel) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long id = JwtUtil.getIdFromToken(token);
        return wheelService.getQuestion(id, difficultyLevel);
    }

    /**
     * 提交答题结果并领奖接口
     */
    @PostMapping("/claim-reward")
    public Result claimReward(
            @RequestHeader("Authorization")  String token,
            @RequestParam("difficultyLevel") Integer difficultyLevel){
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long id = JwtUtil.getIdFromToken(token);
        return wheelService.claimReward(difficultyLevel, id);
    }



}
