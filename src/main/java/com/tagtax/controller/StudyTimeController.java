package com.tagtax.controller;

import com.tagtax.entity.Result;
import com.tagtax.entity.StudyRecord;
import com.tagtax.entity.UserStudyTime;
import com.tagtax.mapper.StudyRecordMapper;
import com.tagtax.mapper.StudyTimeMapper;
import com.tagtax.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/studyTime")
public class StudyTimeController {
    @Autowired
    private StudyTimeMapper studyTimeMapper;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @GetMapping("/getStudyTimeByUserId")
    public Result getStudyTimeByUserId(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        UserStudyTime studyTime = studyTimeMapper.getStudyTimeByUserId(userId);
        if(studyTime == null) {
            return Result.error("查询错误");
        }
        return Result.success(studyTime);
    }

    @GetMapping("/getDailyHoursTop10")
    public Result getDailyHoursTop10(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        List<UserStudyTime> list = studyTimeMapper.getDailyHoursTop10();
        if(list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

    @GetMapping("/getTotalHoursTop10")
    public Result getTotalHoursTop10(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        List<UserStudyTime> list = studyTimeMapper.getTotalHoursTop10();
        if(list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

    @GetMapping("/getPointTop10")
    public Result getPointTop10(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        List<UserStudyTime> list = studyTimeMapper.getPointTop10();
        if(list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

    @GetMapping("/getStudyTimeRecord")
    public Result getStudyTimeRecord(@RequestHeader("Authorization") String token) {
        if(!JwtUtil.validateToken(token.replace("Bearer ", ""))) {
            return Result.error("无效的令牌");
        }
        Long userId = JwtUtil.getIdFromToken(token);
        List<StudyRecord> list = studyRecordMapper.getStudyRecord(userId);
        if(list == null || list.isEmpty()) {
            return Result.error("查询错误");
        }
        return Result.success(list);
    }

}
