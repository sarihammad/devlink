package com.devlink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devlink.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
}