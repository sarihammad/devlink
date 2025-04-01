package com.devlink.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String authorUsername;
    private String content;
    private LocalDateTime createdAt;
}