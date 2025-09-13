package com.tagtax.mapper;

import com.tagtax.entity.UserStudyTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyTimeMapper {

    UserStudyTime getStudyTimeByUserId(Long userId);

    List<UserStudyTime> getDailyHoursTop10();

    List<UserStudyTime> getTotalHoursTop10();

    List<UserStudyTime> getPointTop10();

    Integer updateTotalHoursByUserId(@Param("userId") Long userId, @Param("hours") Double hours);

    Integer updateDailyHoursByUserId(@Param("userId") Long userId, @Param("hours") Double hours);

    Integer updatePointsByUserId(@Param("userId") Long userId, @Param("points") Integer points);

    Integer resetExpiredDailyTime();

    Integer createUserStudyTime(Long userId);


}
