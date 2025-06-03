package com.server.ShareDoo.dto.response;

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
public class ResUserDTO {
    private int userId;
    private String name;
    private String email;
    private String address;
    private String username;

    private Role role;
    private boolean isActive;
    private boolean isVerified;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

}