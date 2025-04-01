package com.devlink.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devlink.dto.UserProfileResponse;
import com.devlink.repository.PostRepository;
import com.devlink.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .github(user.getGithub())
                .skills(user.getSkills())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build())
            .toList();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAnyPost(Long id) {
        postRepository.deleteById(id);
    }
}