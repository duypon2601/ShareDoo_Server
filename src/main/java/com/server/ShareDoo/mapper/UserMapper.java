package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.entity.User;

public class UserMapper {

    public static UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setActive(user.isActive());
        userDTO.setVerified(user.isVerified());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setLastLoginAt(user.getLastLoginAt());
        return userDTO;
    }

    public static User mapToUser(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setUsername(userDTO.getUsername());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        user.setVerified(userDTO.isVerified());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        user.setLastLoginAt(userDTO.getLastLoginAt());
        user.setIsDeleted(false);
        return user;
    }

    public static ResCreateUserDTO mapToResCreateUserDTO(UserDTO userDTO) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        resCreateUserDTO.setUserId(userDTO.getUserId());
        resCreateUserDTO.setName(userDTO.getName());
        resCreateUserDTO.setEmail(userDTO.getEmail());
        resCreateUserDTO.setAddress(userDTO.getAddress());
        resCreateUserDTO.setUsername(userDTO.getUsername());
        resCreateUserDTO.setRole(userDTO.getRole());
        resCreateUserDTO.setActive(userDTO.isActive());
        resCreateUserDTO.setVerified(userDTO.isVerified());
        resCreateUserDTO.setIsDeleted(userDTO.isDeleted());
        resCreateUserDTO.setCreatedAt(userDTO.getCreatedAt());
        resCreateUserDTO.setUpdatedAt(userDTO.getUpdatedAt());
        resCreateUserDTO.setLastLoginAt(userDTO.getLastLoginAt());
        return resCreateUserDTO;
    }
}