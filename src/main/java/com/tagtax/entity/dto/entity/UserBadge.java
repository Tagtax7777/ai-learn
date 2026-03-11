package com.tagtax.entity.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {
    private Long id;
    private Long userId;
    private Long badgeId;
    private LocalDateTime earnedAt;
}