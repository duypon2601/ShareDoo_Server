package com.server.ShareDoo.service.userService;

import com.server.ShareDoo.dto.request.userRequest.CreateUserDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.dto.response.ResUserDTO;
import com.server.ShareDoo.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    // Create
    ResCreateUserDTO createUser(CreateUserDTO createUserDTO) throws IdInvalidException;

    // Read
    List<ResUserDTO> getUserAll() ;
    UserDTO getUserById(Integer userId) throws IdInvalidException;
    UserDTO getUserByUsername(String username) throws IdInvalidException;

    ResUserDTO convertToResUserDTO(UserDTO user);

    Page<ResUserDTO> searchUsers(String username, String email, Pageable pageable);
    Page<UserDTO> getDeletedUsers(Pageable pageable);

    // Update
    UserDTO updateUser(Integer userId, UserDTO userDTO) throws IdInvalidException;
    UserDTO restoreUser(Integer userId) throws IdInvalidException;
    UserDTO activateUser(Integer userId) throws IdInvalidException;
    UserDTO deactivateUser(Integer userId) throws IdInvalidException;
    UserDTO verifyUser(Integer userId) throws IdInvalidException;
    UserDTO updateLastLogin(Integer userId) throws IdInvalidException;

    // Delete
    void deleteUser(Integer userId) throws IdInvalidException;

    // Validation
    boolean isUsernameExist(String username);
    boolean isEmailExist(String email);

//    ResCreateUserDTO convertToResCreateUserDTO(UserDTO user);
//
//    ResUpdateUserDTO convertToResUpdateUserDTO(UserDTO user);
//
//    ResUserDTO convertToResUserDTO(UserDTO user);
    ResCreateUserDTO convertToResCreateUserDTO(UserDTO userDTO);
}
