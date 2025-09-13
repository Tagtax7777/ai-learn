package com.tagtax.service;

import com.tagtax.entity.Result;

public interface CheckInService {
    Result userCheckIn(Long UserId);

    Result getOneYearCheckIn(Long UserId, Integer year);

    Result isCheckIn(Long userId);

    Result getCurrentStreak(Long userId);
}
