package com.tagtax.controller;

import com.tagtax.entity.GoalTasks;
import com.tagtax.entity.LearningGoals;
import com.tagtax.entity.Result;
import com.tagtax.mapper.GoalsMapper;
import com.tagtax.service.GoalsService;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/goals")
public class GoalsController {
    @Autowired
    private GoalsService goalsService;

    @Autowired
    private GoalsMapper goalsMapper;

    @PostMapping("/createGoalsByAi")
    public Result createGoalsByAi(@RequestHeader("Authorization") String token,
                                  @RequestBody String userScanInput) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return goalsService.createGoalsByAi(userScanInput, userId);
    }

    @PostMapping("/cleanDataByAi")
    public Result cleanDataByAi(@RequestHeader("Authorization") String token,
                                @RequestBody String userScanInput) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return goalsService.cleanDataByAi(userScanInput, userId);
    }


    @GetMapping(value = "/talkToAi", produces = "text/html;charset=utf-8")
    public Flux<String> talkToAi(String text) {
        return goalsService.talkToAi(text);
    }



    @PostMapping("/learnTask")
    public Result learnTask(@RequestHeader("Authorization") String token,
                            @RequestParam("id") Long goal_id,
                            @RequestParam("task_id") Long task_id,
                            @RequestParam("hours") Double hours,
                            @RequestParam("startTime") LocalTime startTime,
                            @RequestParam("endTime") LocalTime endTime,
                            @RequestParam("timingDate") LocalDate timingDate) {
        // 验证JWT令牌
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return goalsService.learnTask(userId, goal_id, task_id, hours, startTime, endTime, timingDate);
    }

    @GetMapping("/getGoalsByUserId")
    public Result getGoalsByUserId(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        List<LearningGoals> list = goalsMapper.getGoalsByUserId(userId);
        if (list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

    @GetMapping("/getTasksByGoalId")
    public Result getTasksByGoalId(@RequestHeader("Authorization") String token,
                                   @RequestParam("id") Long goal_id) {
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        List<GoalTasks> list = goalsMapper.getTasksByGoalId(goal_id);
        if (list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

    @DeleteMapping("/deleteGoal")
    public Result deleteGoal(@RequestHeader("Authorization") String token,
                             @RequestParam("id") Long goal_id){
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        if(goalsMapper.deleteGoalsByUserId(goal_id) <= 0){
            return Result.error("删除失败");
        }
        return Result.success("success");
    }

    @PostMapping("/updateTaskEstimatedHour")
    public Result updateTaskEstimatedHour(@RequestHeader("Authorization") String token,
                                          @RequestParam("id") Long taskId,
                                          @RequestParam("hours")  Double hours) {
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        if(goalsMapper.updateTaskEstimatedHours(taskId, hours) <= 0){
            return Result.error("修改失败");
        }
        return Result.success("success");
    }

    @PostMapping("/updateGoalEndDate")
    public Result updateGoalEndDate(@RequestHeader("Authorization") String token,
                                          @RequestParam("id") Long goalId,
                                          @RequestParam("endDate")  LocalDate endDate) {
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        if(goalsMapper.updateGoalEndDate(goalId, endDate) <= 0){
            return Result.error("修改失败");
        }
        return Result.success("success");
    }

    @GetMapping("/getFinishedGoals")
    public Result getFinishedGoals(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        return Result.success(goalsMapper.getFinishedGoals(userId));
    }
}
