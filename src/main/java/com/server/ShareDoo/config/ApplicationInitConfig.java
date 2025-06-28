package com.server.ShareDoo.config;


import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.enums.Role;
import com.server.ShareDoo.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    public ApplicationInitConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {

            if (!userRepository.existsByUsername("string")) {
                User admin = new User();
                admin.setUsername("string");
                admin.setPassword(passwordEncoder.encode("string"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }



        };
    }


}
