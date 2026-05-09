package com.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {

    private long totalUsers;
    private long bannedUsers;
    private long totalPosts;
    private long reportedPosts;
    private long publishedPosts;
    private long deletedPosts;
}
