package com.server.ShareDoo.dto.request.userRequest;

import com.server.ShareDoo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int userId;
    private String name;
    private String email;
    private String address;
    private String imageUrl;
    private String location;
    private String username;
    private String password;
    private Role role;
    private boolean isActive;
    private boolean isVerified;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}