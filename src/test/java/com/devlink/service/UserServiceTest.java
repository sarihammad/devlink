package com.devlink.service;

import com.devlink.dto.FollowResponse;
import com.devlink.dto.UserProfileResponse;
import com.devlink.dto.UserProfileUpdateRequest;
import com.devlink.model.User;
import com.devlink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User currentUser;
    private User targetUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Spring Security context
        when(authentication.getName()).thenReturn("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        currentUser = new User();
        currentUser.setEmail("test@example.com");
        currentUser.setUsername("currentUser");
        currentUser.setFollowing(new HashSet<>());
        currentUser.setFollowers(new HashSet<>());

        targetUser = new User();
        targetUser.setEmail("target@example.com");
        targetUser.setUsername("targetUser");
        targetUser.setFollowing(new HashSet<>());
        targetUser.setFollowers(new HashSet<>());
    }

    @Test
    void shouldFollowAnotherUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(targetUser));

        userService.followUser("targetUser");

        assertTrue(currentUser.getFollowing().contains(targetUser));
        verify(userRepository).save(currentUser);
    }

    @Test
    void shouldUnfollowUser() {
        currentUser.getFollowing().add(targetUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(targetUser));

        userService.unfollowUser("targetUser");

        assertFalse(currentUser.getFollowing().contains(targetUser));
        verify(userRepository).save(currentUser);
    }

    @Test
    void shouldReturnFollowers() {
        User follower1 = new User();
        follower1.setUsername("alice");
        follower1.setBio("I love dev");
        follower1.setAvatarUrl("/uploads/alice.png");

        User follower2 = new User();
        follower2.setUsername("bob");
        follower2.setBio("Spring wizard");
        follower2.setAvatarUrl("/uploads/bob.png");

        targetUser.getFollowers().add(follower1);
        targetUser.getFollowers().add(follower2);

        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(targetUser));

        List<FollowResponse> followers = userService.getFollowers("targetUser");

        assertEquals(2, followers.size());
        assertEquals("alice", followers.get(0).getUsername());
        assertEquals("bob", followers.get(1).getUsername());
    }

    @Test
    void shouldReturnFollowing() {
        User following1 = new User();
        following1.setUsername("charlie");
        following1.setBio("Backend dev");
        following1.setAvatarUrl("/uploads/charlie.png");

        User following2 = new User();
        following2.setUsername("diana");
        following2.setBio("Frontend pro");
        following2.setAvatarUrl("/uploads/diana.png");

        targetUser.getFollowing().add(following1);
        targetUser.getFollowing().add(following2);

        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(targetUser));

        List<FollowResponse> following = userService.getFollowing("targetUser");

        assertEquals(2, following.size());
        assertEquals("charlie", following.get(0).getUsername());
        assertEquals("diana", following.get(1).getUsername());
    }

    @Test
    void shouldUpdateUserProfile() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));

        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setBio("Updated bio");
        request.setGithub("https://github.com/new");
        request.setSkills(List.of("Java, Spring"));
        request.setAvatarUrl("/uploads/new.png");

        userService.updateUserProfile(request);

        assertEquals("Updated bio", currentUser.getBio());
        assertEquals("https://github.com/new", currentUser.getGithub());
        assertEquals("Java, Spring", currentUser.getSkills());
        assertEquals("/uploads/new.png", currentUser.getAvatarUrl());
        verify(userRepository).save(currentUser);
    }

    @Test
    void shouldReturnCurrentUserProfile() {
        User user = new User();
        user.setUsername("sarihammad");
        user.setEmail("test@example.com");
        user.setBio("Java dev");
        user.setGithub("https://github.com/sarihammad");
        user.setSkills(List.of("Spring Boot", "Docker"));
        user.setAvatarUrl("/uploads/avatar.png");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserProfileResponse response = userService.getCurrentUserProfile();

        assertEquals("sarihammad", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Java dev", response.getBio());
        assertEquals("https://github.com/sarihammad", response.getGithub());
        assertEquals("Spring Boot, Docker", response.getSkills());
        assertEquals("/uploads/avatar.png", response.getAvatarUrl());
    }
}