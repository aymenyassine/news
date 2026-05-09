package com.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.news.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private Role role;
    private boolean banned;
    private String banReason;
    private LocalDateTime bannedAt;
    private LocalDateTime createdAt;
    private long postCount;
}
