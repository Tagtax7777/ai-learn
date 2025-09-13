package com.tagtax.service.serviceImpl;

import com.tagtax.entity.Badge;
import com.tagtax.entity.Result;
import com.tagtax.entity.dto.MonthDays;
import com.tagtax.mapper.BadgeMapper;
import com.tagtax.mapper.CheckInMapper;
import com.tagtax.mapper.StudyTimeMapper;
import com.tagtax.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private StudyTimeMapper studyTimeMapper;

    @Autowired
    private BadgeMapper badgeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result userCheckIn(Long userId) {
        // 获取当前时间
        LocalDate today = LocalDate.now();
        // 初始化连续签到记录
        int newStreak = 0;
        // 检查今日是否签到
        if (checkInMapper.findTodayCheckIn(userId) > 0){
            return Result.error("今日已签到");
        }
        // 插入签到记录
        if(checkInMapper.addOneCheckIn(userId) > 0){
            // 获取最后签到的日期
            LocalDate lastCheckinDate = checkInMapper.findLastCheckIn(userId);
            // 计算连续签到天数
            if(lastCheckinDate == null){ // 没有记录，第一次签到
                newStreak = 1;
            }else if(lastCheckinDate.equals(today.minusDays(1))){ // 昨天签到了，签到数+1
                Integer currentStreak = checkInMapper.getCurrentStreak(userId);
                newStreak = currentStreak + 1;
            }
            // 更新（新插入）连续签到记录
            if(checkInMapper.updateStreak(userId, newStreak, today) > 0){
                Integer point = studyTimeMapper.getStudyTimeByUserId(userId).getPoints() + 20;
                studyTimeMapper.updatePointsByUserId(userId, point);

                // 检查可获得的徽章
                List<Badge> eligibleBadges = badgeMapper.findByPointsThreshold(point);

                // 授予新徽章
                eligibleBadges.forEach(badge -> {
                    if (!badgeMapper.exists(userId, badge.getId())) {
                        badgeMapper.addUserBadge(userId, badge.getId());
                    }
                });

                return Result.success("签到成功");
            }else {
                return Result.error("插入签到记录失败");
            }
        }else {
            return Result.error("插入签到记录失败");
        }
    }

    @Override
    public Result getOneYearCheckIn(Long userId, Integer year) {
        List<LocalDate> dates = checkInMapper.findOneYearCheckIn(userId, year);
        if (dates == null){
            return Result.error("查询失败");
        }
        // 1. 按月份分组
        Map<Integer, List<Integer>> monthToDaysMap = dates.stream()
                .collect(Collectors.groupingBy(
                        LocalDate::getMonthValue,
                        Collectors.mapping(LocalDate::getDayOfMonth, Collectors.toList())
                ));

        // 2. 转换为前端需要的格式
        List<MonthDays> monthDays = monthToDaysMap.entrySet().stream()
                .map(entry -> new MonthDays(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(MonthDays::getMonth).reversed()) // 按月份降序排列
                .toList();
        return Result.success(monthDays);
    }

    @Override
    public Result isCheckIn(Long userId) {
        if(checkInMapper.findTodayCheckIn(userId) > 0){
            return Result.success("今日已签到");
        }else {
            return Result.error("今日未签到");
        }
    }

    @Override
    public Result getCurrentStreak(Long userId) {
        if(checkInMapper.getCurrentStreak(userId) != null){
            Integer currentStreak = checkInMapper.getCurrentStreak(userId);
            return Result.success(currentStreak);
        }else {
            return Result.success(0);
        }
    }
}