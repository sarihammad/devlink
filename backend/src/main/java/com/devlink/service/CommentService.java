package com.devlink.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devlink.dto.CommentRequest;
import com.devlink.dto.CommentResponse;
import com.devlink.model.Comment;
import com.devlink.model.Post;
import com.devlink.model.User;
import com.devlink.repository.CommentRepository;
import com.devlink.repository.PostRepository;
import com.devlink.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void addComment(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = getCurrentUser();

        Comment comment = Comment.builder()
            .post(post)
            .author(user)
            .content(request.getContent())
            .build();

        commentRepository.save(comment);
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
            .map(comment -> CommentResponse.builder()
                .id(comment.getId())
                .authorUsername(comment.getAuthor().getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build())
            .toList();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}