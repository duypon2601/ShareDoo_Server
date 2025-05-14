package com.server.ShareDoo.dto.response;

import com.server.ShareDoo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResLoginDTO {
    private String token;
    private int userId;
    private String fullName;
    private String email;
    private String username;
    private Role role;
    private String phone;
    private String address;
    private boolean verified;
    private String avatarUrl;
    private boolean active;
}