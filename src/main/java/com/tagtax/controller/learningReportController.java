package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.service.LearningReportService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/learningReport")
public class learningReportController {

    @Autowired
    private LearningReportService learningReportService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 获取最新学习报告
     */
    @GetMapping("/latest")
    public Result getLatestReport(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return learningReportService.getLatestLearningReport(userId);
    }
    
    /**
     * 清除Redis缓存中的学习报告
     */
    @DeleteMapping("/clearCache")
    public Result clearCache(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        
        // 清除用户的学习报告缓存
        String cacheKeyPattern = "learning_report:" + userId + ":*";
        redisTemplate.keys(cacheKeyPattern).forEach(key -> {
            redisTemplate.delete(key);
        });
        
        return Result.success("学习报告缓存已清除");
    }
}
