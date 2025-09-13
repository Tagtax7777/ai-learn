package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.mapper.BadgeMapper;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/badge")
public class BadgeController {
    @Autowired
    private BadgeMapper badgeMapper;

    @GetMapping("/getAllBadges")
    public Result getAllBadges(@RequestHeader("Authorization") String token){
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(badgeMapper.findAllBadges());
    }

    @GetMapping("/getUserBadges")
    public Result getUserBadges(@RequestHeader("Authorization") String token){
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(badgeMapper.findUserBadges(userId));
    }

    @GetMapping("/getUnearnedBadges")
    public Result getUnearnedBadges(@RequestHeader("Authorization") String token){
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(badgeMapper.findUnearnedBadges(userId));
    }

    @GetMapping("/countUserBadges")
    public Result countUserBadges(@RequestHeader("Authorization") String token){
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(badgeMapper.countUserBadges(userId));
    }


}
