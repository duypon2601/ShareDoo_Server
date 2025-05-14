package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.dto.response.RestResponse;
import com.server.ShareDoo.service.userService.UserService;
import com.server.ShareDoo.util.error.IdInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "api")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all users",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied"
        )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getUserAll();
        RestResponse<List<UserDTO>> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(users);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the user",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<RestResponse<UserDTO>> getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable Integer id
    ) throws IdInvalidException {
        UserDTO user = userService.getUserById(id);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(user);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Create new user",
        description = "Creates a new user in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<UserDTO>> createUser(
        @Parameter(description = "User data", required = true)
        @Valid @RequestBody UserDTO userDTO
    ) throws IdInvalidException {
        UserDTO createdUser = userService.createUser(userDTO);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setData(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Update user",
        description = "Updates an existing user's information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<RestResponse<UserDTO>> updateUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Integer id,
        @Parameter(description = "Updated user data", required = true)
        @Valid @RequestBody UserDTO userDTO
    ) {
        UserDTO updatedUser = userService.updateUser(userDTO, id);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(updatedUser);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete user",
        description = "Soft deletes a user from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<Void>> deleteUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Integer id
    ) throws IdInvalidException {
        userService.deleteUser(id);
        RestResponse<Void> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get user by username",
        description = "Retrieves a user by their username"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the user",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponse<UserDTO>> getUserByUsername(
        @Parameter(description = "Username", required = true)
        @PathVariable String username
    ) {
        UserDTO user = userService.handleGetUserByUsername(username);
        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setData(user);
        return ResponseEntity.ok(response);
    }
} 