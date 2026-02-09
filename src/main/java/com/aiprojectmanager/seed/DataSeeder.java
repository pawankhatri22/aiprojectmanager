package com.aiprojectmanager.seed;

import com.aiprojectmanager.user.Role;
import com.aiprojectmanager.user.User;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@local.com").isEmpty()) {
            userRepository.save(User.builder().email("admin@local.com").password(encoder.encode("admin123"))
                    .role(Role.ADMIN).enabled(true).createdAt(LocalDateTime.now()).build());
        }
    }
}
