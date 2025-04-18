package com.devlink.controller;

import com.devlink.dto.CommentRequest;
import com.devlink.dto.CommentResponse;
import com.devlink.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CommentService commentService;

    @Test
    void shouldAddComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setContent("Nice post!");

        mockMvc.perform(post("/api/posts/10/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        verify(commentService).addComment(eq(10L), any(CommentRequest.class));
    }

    @Test
    void shouldGetCommentsForPost() throws Exception {
        CommentResponse comment = CommentResponse.builder()
            .id(1L)
            .content("Interesting post!")
            .authorUsername("sarihammad")
            .createdAt(LocalDateTime.now())
            .build();

        when(commentService.getComments(5L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/posts/5/comments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].content").value("Interesting post!"))
            .andExpect(jsonPath("$[0].username").value("sarihammad"));
    }
}