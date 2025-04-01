package com.devlink.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
}