package com.devlink.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devlink.dto.UserProfileResponse;
import com.devlink.dto.UserProfileUpdateRequest;
import com.devlink.model.User;
import com.devlink.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .bio(user.getBio())
            .github(user.getGithub())
            .skills(user.getSkills())
            .avatarUrl(user.getAvatarUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public void updateUserProfile(UserProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBio(request.getBio());
        user.setGithub(request.getGithub());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setSkills(request.getSkills());

        userRepository.save(user);
    }
}