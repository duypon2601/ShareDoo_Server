package com.server.ShareDoo.service.authService;

import com.server.ShareDoo.dto.request.LoginDTO;
import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.dto.response.ResLoginDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.enums.Role;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.service.userService.UserService;
import com.server.ShareDoo.util.SecurityUtil;
import com.server.ShareDoo.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    UserService userService;

    public ResLoginDTO login(LoginDTO loginDTO) {
        var user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Wrong password");
        }
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        String token = securityUtil.createToken(user);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setToken(token);
        resLoginDTO.setRole(user.getRole());
        resLoginDTO.setUsername(user.getUsername());
        resLoginDTO.setEmail(user.getEmail());
        resLoginDTO.setAddress(user.getAddress());
        resLoginDTO.setPhone(user.getPhone());
        resLoginDTO.setFullName(user.getFullName());
        resLoginDTO.setUserId(user.getUserId());
        resLoginDTO.setVerified(user.isVerified());
        resLoginDTO.setAvatarUrl(user.getAvatarUrl());
        resLoginDTO.setActive(user.isActive());
        return resLoginDTO;
    }

    public User register(UserDTO userDTO) throws IdInvalidException {
        User user = User.builder()
                .role(Role.ADMIN)
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .userId(userDTO.getUserId())
                .phone(userDTO.getPhone())
                .address(userDTO.getAddress())
                .isVerified(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        boolean isUsernameExist = this.userService.isUsernameExist(userDTO.getUsername());
        if (isUsernameExist) {
            throw new IdInvalidException(
                    "Username " + userDTO.getUsername() + " đã tồn tại, vui lòng sử dụng email khác.");
        }

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("user.UK_sb8bbouer5wak8vyiiy4pf2bx")) 
                throw new DataIntegrityViolationException("Duplicate UserName");
            else if (e.getMessage().contains("user.UK_ob8kqyqqgmefl0aco34akdtpe"))
                throw new DataIntegrityViolationException("Duplicate Email");
            else 
                throw new DataIntegrityViolationException("Duplicate Phone");
        }
    }

    public ResLoginDTO createUser(UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .fullName(userDTO.getFullName())
                .userId(userDTO.getUserId())
                .role(userDTO.getRole())
                .phone(userDTO.getPhone())
                .address(userDTO.getAddress())
                .isVerified(false)
                .isActive(true)
                .isDeleted(false)
                .build();

        boolean isUsernameExist = this.userService.isUsernameExist(userDTO.getUsername());
        if (isUsernameExist) {
            throw new DataIntegrityViolationException("Duplicate Username");
        } else {
            boolean isEmailExist = this.userService.isEmailExist(userDTO.getEmail());
            if (isEmailExist) {
                throw new DataIntegrityViolationException("Duplicate Email");
            } else {
                boolean isPhoneExist = this.userService.isPhoneExist(userDTO.getPhone());
                if (isPhoneExist) {
                    throw new DataIntegrityViolationException("Duplicate Phone");
                }
            }
        }

        user = userRepository.save(user);
        return login(new LoginDTO(user.getUsername(), userDTO.getPassword()));
    }

    public boolean verifyUser(String token) {
        // Implementation of verifyUser method
        return false; // Placeholder return, actual implementation needed
    }

    public boolean resendVerificationEmail(String email) {
        // Implementation of resendVerificationEmail method
        return false; // Placeholder return, actual implementation needed
    }

    public boolean forgotPassword(String email) {
        // Implementation of forgotPassword method
        return false; // Placeholder return, actual implementation needed
    }

    public boolean resetPassword(String token, String newPassword) {
        // Implementation of resetPassword method
        return false; // Placeholder return, actual implementation needed
    }
}

