package com.server.ShareDoo.config;

import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.enums.Role;
import com.server.ShareDoo.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    public ApplicationInitConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN)
                        .fullName("Administrator")
                        .email("admin@sharedoo.com")
                        .phone("0123456789")
                        .isVerified(true)
                        .isActive(true)
                        .isDeleted(false)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
