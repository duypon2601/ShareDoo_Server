package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.entity.User;

public class UserMapper {

    public static UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddress(user.getAddress());
        userDTO.setVerified(user.isVerified());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setActive(user.isActive());
        userDTO.setDeleted(user.isDeleted());
        return userDTO;
    }

    public static User mapToUser(UserDTO userDTO) {
        return User.builder()
                .userId(userDTO.getUserId())
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .phone(userDTO.getPhone())
                .address(userDTO.getAddress())
                .isVerified(userDTO.isVerified())
                .avatarUrl(userDTO.getAvatarUrl())
                .isActive(userDTO.isActive())
                .isDeleted(userDTO.isDeleted())
                .build();
    }
}