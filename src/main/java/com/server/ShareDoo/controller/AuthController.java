package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.LoginDTO;
import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.dto.response.ResLoginDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.service.authService.AuthService;
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
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("api")
@SecurityRequirement(name = "api")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    AuthService authService;

//    @Autowired
//    OtpService otpService;


    private final UserService userService ;

    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = ResLoginDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid credentials"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(
        @Parameter(description = "Login credentials", required = true)
        @Valid @RequestBody LoginDTO loginDTO
    ) {
        ResLoginDTO res = authService.login(loginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    @Operation(
        summary = "User registration",
        description = "Registers a new user in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = User.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<User> createUser(
        @Parameter(description = "User registration data", required = true)
        @Valid @RequestBody UserDTO userDTO
    ) throws IdInvalidException {
//        boolean isOtpValid = otpService.validateOtp(userDTO.getPhone(), otp);
//        if (!isOtpValid) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
        User user = authService.register(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


//    @PostMapping("/login/student")
//    public ResponseEntity<ResLoginDTO> loginStudent(@Valid @RequestBody LoginDTO loginDTO) {
//        ResLoginDTO res = authService.loginStudent(loginDTO);
//        return ResponseEntity.status(HttpStatus.OK).body(res);
//    }


//    @PostMapping("/sendOtp")
//    public ResponseEntity<String> sendOtp(@RequestParam String phoneNumber) {
//        String otp = otpService.generateOtp(phoneNumber);
//        return new ResponseEntity<>("OTP sent successfully", HttpStatus.OK);
//    }
//
//    @PostMapping("/verifyOtp")
//    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
//        boolean isValid = otpService.validateOtp(phoneNumber, otp);
//        if (isValid) {
//            return new ResponseEntity<>("OTP verified successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Invalid OTP", HttpStatus.BAD_REQUEST);
//        }
//    }
}

