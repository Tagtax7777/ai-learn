package com.tagtax.controller;

import com.tagtax.entity.Level;
import com.tagtax.entity.Question;
import com.tagtax.entity.Result;
import com.tagtax.entity.UserLevelRecord;
import com.tagtax.entity.dto.StartLevelRequest;
import com.tagtax.entity.dto.SubmitAnswersRequest;
import com.tagtax.mapper.LevelMapper;
import com.tagtax.service.LevelService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/levels")
public class LevelController {
    
    @Autowired
    private LevelService levelService;

    @Autowired
    private LevelMapper  levelMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 获取用户可用关卡列表
     */
    @GetMapping("/getUserLevels")
    public Result getUserLevels(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return levelService.getUserAvailableLevels(userId);
    }
    
    /**
     * 开始关卡挑战(AI生成题目)
     */
    @PostMapping("/startLevel")
    public Result startLevel(@RequestHeader("Authorization") String token,
                            @RequestBody StartLevelRequest request) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return levelService.generateLevelQuestions(userId, request.getLevelId(), request.getGoalIds());
    }
    
    /**
     * 提交答案并获取结果
     */
    @PostMapping("/submitAnswers")
    public Result submitAnswers(@RequestHeader("Authorization") String token,
                               @RequestBody SubmitAnswersRequest request) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return levelService.evaluateUserAnswers(userId, request.getLevelId(), request.getUserAnswers());
    }
    
    /**
     * 获取关卡历史记录
     */
    @GetMapping("/getLevelHistory")
    public Result getLevelHistory(@RequestHeader("Authorization") String token,
                                 @RequestParam("levelId") Long levelId) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return levelService.getUserLevelHistory(userId, levelId);
    }

    /**
     * 获取关卡所有历史记录
     */
    @GetMapping("/getAllHistory")
    public Result getAllHistory(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(levelMapper.getAllUserLevelRecords(userId));
    }
    
    /**
     * 获取所有关卡信息(管理员用)
     */
    @GetMapping("/getAllLevels")
    public Result getAllLevels(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        return levelService.getAllLevels();
    }
    
    /**
     * 获取用户每日挑战统计
     */
    @GetMapping("/getDailyStats")
    public Result getDailyStats(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return levelService.getUserDailyStats(userId);
    }
    
    /**
     * 清除Redis缓存中的关卡数据
     */
    @DeleteMapping("/clearCache")
    public Result clearCache(@RequestHeader("Authorization") String token) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        
        // 清除用户的关卡题目缓存
        String questionsKeyPattern = "daily:questions:" + userId + ":*";
        redisTemplate.keys(questionsKeyPattern).forEach(key -> {
            redisTemplate.delete(key);
        });
        
        // 清除用户的关卡进度缓存
        String progressKeyPattern = "daily:progress:" + userId + ":*";
        redisTemplate.keys(progressKeyPattern).forEach(key -> {
            redisTemplate.delete(key);
        });
        
        return Result.success("关卡缓存已清除");
    }
}