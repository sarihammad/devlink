package com.devlink.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devlink.dto.PostRequest;
import com.devlink.dto.PostResponse;
import com.devlink.model.Post;
import com.devlink.model.User;
import com.devlink.repository.PostRepository;
import com.devlink.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(PostRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
            .content(request.getContent())
            .author(user)
            .build();

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::mapToResponse)
            .toList();
    }

    public PostResponse getPost(Long id) {
        return postRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!post.getAuthor().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        post.setContent(request.getContent());
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!post.getAuthor().getEmail().equals(currentEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        postRepository.delete(post);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
            .id(post.getId())
            .content(post.getContent())
            .authorUsername(post.getAuthor().getUsername())
            .createdAt(post.getCreatedAt())
            .build();
    }

    public void likePost(Long postId) {
        Post post = getPostEntity(postId);
        User user = getCurrentUser();
    
        post.getLikedBy().add(user);
        postRepository.save(post);
    }
    
    public void unlikePost(Long postId) {
        Post post = getPostEntity(postId);
        User user = getCurrentUser();
    
        post.getLikedBy().remove(user);
        postRepository.save(post);
    }
    
    public List<String> getPostLikers(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"))
            .getLikedBy().stream()
            .map(User::getUsername)
            .toList();
    }
    
    private Post getPostEntity(Long id) {
        return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}