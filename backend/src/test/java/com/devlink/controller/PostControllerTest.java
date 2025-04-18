package com.devlink.controller;

import com.devlink.dto.PostRequest;
import com.devlink.dto.PostResponse;
import com.devlink.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PostService postService;

    @Test
    void shouldCreatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setContent("Hello World!");

        PostResponse response = PostResponse.builder()
            .id(1L)
            .content("Hello World!")
            .authorUsername("sarihammad")
            .createdAt(LocalDateTime.now())
            .build();

        when(postService.createPost(any(PostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.content").value("Hello World!"));
    }

    @Test
    void shouldGetAllPosts() throws Exception {
        PostResponse post = PostResponse.builder()
            .id(1L)
            .content("First Post")
            .authorUsername("sarihammad")
            .createdAt(LocalDateTime.now())
            .build();

        when(postService.getAllPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].content").value("First Post"));
    }

    @Test
    void shouldGetPostById() throws Exception {
        PostResponse post = PostResponse.builder()
            .id(2L)
            .content("Test Post")
            .authorUsername("someone")
            .createdAt(LocalDateTime.now())
            .build();

        when(postService.getPost(2L)).thenReturn(post);

        mockMvc.perform(get("/api/posts/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.content").value("Test Post"));
    }

    @Test
    void shouldUpdatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setContent("Updated content");

        mockMvc.perform(put("/api/posts/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());

        verify(postService).updatePost(eq(5L), any(PostRequest.class));
    }

    @Test
    void shouldDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/5"))
            .andExpect(status().isNoContent());

        verify(postService).deletePost(5L);
    }

    @Test
    void shouldLikePost() throws Exception {
        mockMvc.perform(post("/api/posts/7/like"))
            .andExpect(status().isOk());

        verify(postService).likePost(7L);
    }

    @Test
    void shouldUnlikePost() throws Exception {
        mockMvc.perform(delete("/api/posts/7/like"))
            .andExpect(status().isOk());

        verify(postService).unlikePost(7L);
    }

    @Test
    void shouldReturnLikers() throws Exception {
        when(postService.getPostLikers(3L)).thenReturn(List.of("alice", "bob"));

        mockMvc.perform(get("/api/posts/3/likes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("alice"))
            .andExpect(jsonPath("$[1]").value("bob"));
    }
}