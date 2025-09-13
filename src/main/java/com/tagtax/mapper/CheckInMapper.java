package com.tagtax.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckInMapper {
    // 插入签到记录
    Integer addOneCheckIn(Long userId);

    // 查询签到记录(查询用户某年的所有签到记录日期)
    List<LocalDate> findOneYearCheckIn(Long userId, Integer year);

    // 查询今日用户签到状态
    Integer findTodayCheckIn(Long userId);

    // 获取用户最后签到日期
    LocalDate findLastCheckIn(Long userId);

    // 更新连续签到记录
    Integer updateStreak(Long userId, Integer newStreak, LocalDate today);

    // 查询连续签到天数
    Integer getCurrentStreak(Long userId);

}
