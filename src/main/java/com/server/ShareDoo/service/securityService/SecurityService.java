package com.server.ShareDoo.service.securityService;

import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public boolean isCurrentUser(Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElse(null);

        return user != null && user.getUserId() == userId;
    }
} 