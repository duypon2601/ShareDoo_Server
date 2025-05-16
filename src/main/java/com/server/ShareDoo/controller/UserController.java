package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.userRequest.CreateUserDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.dto.response.RestResponse;
import com.server.ShareDoo.service.userService.UserService;
import com.server.ShareDoo.util.error.IdInvalidException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "User Management", description = "User management APIs")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResCreateUserDTO> createUser(@RequestBody CreateUserDTO createUserDTO) throws IdInvalidException {
        ResCreateUserDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<UserDTO>> getUserById(@PathVariable Integer id) throws IdInvalidException {
        UserDTO user = userService.getUserById(id);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User retrieved successfully");
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<RestResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        RestResponse<Page<UserDTO>> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Users retrieved successfully");
        response.setData(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleted")
    public ResponseEntity<RestResponse<Page<UserDTO>>> getDeletedUsers(Pageable pageable) {
        Page<UserDTO> deletedUsers = userService.getDeletedUsers(pageable);
        RestResponse<Page<UserDTO>> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Deleted users retrieved successfully");
        response.setData(deletedUsers);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<UserDTO>> updateUser(
        @PathVariable Integer id,
        @RequestBody UserDTO userDTO) throws IdInvalidException {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User updated successfully");
        response.setData(updatedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<RestResponse<UserDTO>> restoreUser(@PathVariable Integer id) throws IdInvalidException {
        UserDTO restoredUser = userService.restoreUser(id);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User restored successfully");
        response.setData(restoredUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable Integer id) throws IdInvalidException {
        userService.deleteUser(id);
        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User deleted successfully");
        return ResponseEntity.ok(response);
    }
} 