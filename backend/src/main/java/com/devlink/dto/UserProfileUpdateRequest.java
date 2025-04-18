package com.devlink.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String bio;
    private String github;
    private List<String> skills;
    private String avatarUrl;
}