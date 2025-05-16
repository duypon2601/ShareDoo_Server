package com.server.ShareDoo.dto.request.userRequest;

import com.server.ShareDoo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    private String name;
    private String email;
    private String address;
    private String username;
    private String password;
    private Role role;
}
