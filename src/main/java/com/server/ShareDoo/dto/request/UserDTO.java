package com.server.ShareDoo.dto.request;


import com.server.ShareDoo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int user_id;

    private String name;

    private String restaurant_name;

    private String email;

//    private String address;
//
//    private String image;

    private String username;

    private String password;

//    private Boolean delete;

    private Role role;


}