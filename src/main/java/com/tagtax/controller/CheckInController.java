package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.service.CheckInService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Year;


@CrossOrigin
@RestController
@RequestMapping("/checkIn")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    //  用户签到
    @PostMapping("/userCheckIn")
    public Result userCheckIn(@RequestHeader("Authorization") String token){
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return checkInService.userCheckIn(userId);
    }

    // 查询某个用户一年的所有签到记录
    @GetMapping("/getOneYearCheckIn")
    public Result getOneYearCheckIn(@RequestHeader("Authorization") String token,
                                    @RequestParam("year") Integer year){
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return checkInService.getOneYearCheckIn(userId, year);
    }

    // 查询某用户今日是否签到
    @GetMapping("/isCheckIn")
    public Result isCheckIn(@RequestHeader("Authorization") String token){
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return checkInService.isCheckIn(userId);
    }

    // 查询某用户连续签到天数
    @GetMapping("/getCurrentStreak")
    public Result getCurrentStreak(@RequestHeader("Authorization") String token){
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return checkInService.getCurrentStreak(userId);
    }

}
