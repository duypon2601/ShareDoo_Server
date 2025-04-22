package com.server.ShareDoo.service.userService;


import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.util.error.IdInvalidException;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO) throws IdInvalidException;

    UserDTO getUserById ( Integer user_id) throws IdInvalidException;

    List<UserDTO> getUserAll();

    UserDTO updateUser(UserDTO updateUser, Integer topic_id);

    void deleteUser (Integer user_id) throws IdInvalidException;

    UserDTO handleGetUserByUsername(String username);

    boolean isUsernameExist (String username);

//    ResCreateUserDTO convertToResCreateUserDTO(UserDTO user);
//
//    ResUpdateUserDTO convertToResUpdateUserDTO(UserDTO user);
//
//    ResUserDTO convertToResUserDTO(UserDTO user);
}
