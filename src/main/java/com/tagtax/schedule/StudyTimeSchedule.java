package com.tagtax.schedule;

import com.tagtax.mapper.StudyTimeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class StudyTimeSchedule {
    private final StudyTimeMapper studyTimeMapper;

    // 每天凌晨1点重置过期数据
    @Scheduled(cron = "0 0 1 * * ?")
    public void resetDailyStudyTime() {
        studyTimeMapper.resetExpiredDailyTime();
    }


}