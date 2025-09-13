package com.tagtax.service;

import com.tagtax.entity.Result;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;

public interface GoalsService {


    Result createGoalsByAi(String userScanInput, Long userId);

    Result learnTask(Long userId, Long goalId, Long taskId, Double hours,
                     LocalTime startTime, LocalTime endTime, LocalDate timingDate);

    Flux<String> talkToAi(String text);

    Result cleanDataByAi(String userScanInput, Long userId);
}
