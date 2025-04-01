package com.devlink.controller;

import com.devlink.dto.FollowResponse;
import com.devlink.dto.UserProfileResponse;
import com.devlink.dto.UserProfileUpdateRequest;
import com.devlink.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldGetCurrentUserProfile() throws Exception {
        UserProfileResponse response = UserProfileResponse.builder()
            .username("sarihammad")
            .email("sarihammad@example.com")
            .bio("Developer")
            .github("https://github.com/sarihammad")
            .skills(List.of("Java, Spring"))
            .avatarUrl("/uploads/avatar.png")
            .createdAt(LocalDateTime.now())
            .build();

        when(userService.getCurrentUserProfile()).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("sarihammad"))
            .andExpect(jsonPath("$.email").value("sarihammad@example.com"));
    }

    @Test
    void shouldUpdateMyProfile() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setBio("Updated bio");
        request.setGithub("https://github.com/sarihammad");
        request.setSkills(List.of("Java, Spring Boot"));
        request.setAvatarUrl("/uploads/avatar-new.png");

        mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        verify(userService).updateUserProfile(any(UserProfileUpdateRequest.class));
    }

    @Test
    void shouldFollowUser() throws Exception {
        mockMvc.perform(post("/api/users/someuser/follow"))
            .andExpect(status().isOk());

        verify(userService).followUser("someuser");
    }

    @Test
    void shouldUnfollowUser() throws Exception {
        mockMvc.perform(delete("/api/users/someuser/unfollow"))
            .andExpect(status().isOk());

        verify(userService).unfollowUser("someuser");
    }

    @Test
    void shouldReturnFollowers() throws Exception {
        FollowResponse follower = FollowResponse.builder()
            .username("alice")
            .bio("bio")
            .avatarUrl("/uploads/alice.png")
            .build();

        when(userService.getFollowers("someuser")).thenReturn(List.of(follower));

        mockMvc.perform(get("/api/users/someuser/followers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void shouldReturnFollowing() throws Exception {
        FollowResponse following = FollowResponse.builder()
            .username("bob")
            .bio("cool dev")
            .avatarUrl("/uploads/bob.png")
            .build();

        when(userService.getFollowing("someuser")).thenReturn(List.of(following));

        mockMvc.perform(get("/api/users/someuser/following"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("bob"));
    }

    @Test
    void shouldUploadAvatar() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
            "file", "avatar.png", "image/png", "fake image content".getBytes()
        );

        when(userService.uploadAvatar(any())).thenReturn("/uploads/avatar.png");

        mockMvc.perform(multipart("/api/users/me/avatar")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.avatarUrl").value("/uploads/avatar.png"));

        verify(userService).uploadAvatar(any());
    }
}