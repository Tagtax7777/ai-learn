package com.tagtax.entity;

import lombok.*;
import java.time.LocalDate;
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