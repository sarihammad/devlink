package com.devlink.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String bio;
    private String avatarUrl;

    private String github;

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    private boolean isAdmin = false;

    private LocalDateTime createdAt;
}