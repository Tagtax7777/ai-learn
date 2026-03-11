package com.tagtax.entity.dto.entity;

import lombok.Data;

@Data
public class Badge {
    private Long id;
    private String badgeName;
    private String badgeImage;
    private String badgeImageLocked;
    private Integer requiredPoints;
}