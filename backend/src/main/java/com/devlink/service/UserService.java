package com.devlink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.devlink.dto.FollowResponse;
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

    public void followUser(String usernameToFollow) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User targetUser = userRepository.findByUsername(usernameToFollow)
            .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (currentUser.equals(targetUser)) {
            throw new RuntimeException("You cannot follow yourself.");
        }

        currentUser.getFollowing().add(targetUser);
        userRepository.save(currentUser);
    }

    public void unfollowUser(String usernameToUnfollow) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User targetUser = userRepository.findByUsername(usernameToUnfollow)
            .orElseThrow(() -> new RuntimeException("Target user not found"));

        currentUser.getFollowing().remove(targetUser);
        userRepository.save(currentUser);
    }

    public List<FollowResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowers().stream()
            .map(f -> FollowResponse.builder()
                .username(f.getUsername())
                .bio(f.getBio())
                .avatarUrl(f.getAvatarUrl())
                .build())
            .toList();
    }

    public List<FollowResponse> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowing().stream()
            .map(f -> FollowResponse.builder()
                .username(f.getUsername())
                .bio(f.getBio())
                .avatarUrl(f.getAvatarUrl())
                .build())
            .toList();
    }
    public String uploadAvatar(MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create unique filename
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;

        // Save file to /uploads
        Path uploadPath = Paths.get("uploads/" + filename);
        try {
            Files.write(uploadPath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save avatar");
        }

        // Save avatar URL
        user.setAvatarUrl("/uploads/" + filename);
        userRepository.save(user);

        return user.getAvatarUrl();
    }
}