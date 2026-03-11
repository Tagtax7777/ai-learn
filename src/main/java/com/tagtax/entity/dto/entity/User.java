package com.tagtax.entity.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;                    // 主键ID
    private String username;            // 用户名
    private String phone;               // 手机号
    private String avatarUrl;
    private LocalDateTime createdAt;     // 创建时间
}