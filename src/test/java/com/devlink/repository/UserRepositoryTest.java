package com.devlink.repository;

import com.devlink.AbstractIntegrationTest;
import com.devlink.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFetchUserByEmail() {
        User user = new User();
        user.setUsername("sarihammad");
        user.setEmail("sarihammad@example.com");
        user.setPassword("secure");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("sarihammad@example.com");

        assertTrue(found.isPresent());
        assertEquals("sarihammad", found.get().getUsername());
    }
}