package com.server.ShareDoo.service.authService;

import com.server.ShareDoo.dto.request.userRequest.LoginDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResLoginDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.mapper.UserMapper;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.service.userService.UserService;
import com.server.ShareDoo.util.SecurityUtil;
import com.server.ShareDoo.util.error.IdInvalidException;
import com.server.ShareDoo.util.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private UserService userService;

    public ResLoginDTO login(LoginDTO loginDTO) {
        var user = userRepository.findByUsername(loginDTO.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) throw new NotFoundException("Wrong password");
        String token = securityUtil.createToken(user);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setToken(token);
        resLoginDTO.setUsername(user.getUsername());
        resLoginDTO.setRole(user.getRole());
        resLoginDTO.setEmail(user.getEmail());
        resLoginDTO.setName(user.getName());
        return resLoginDTO;
    }

    public UserDTO register(UserDTO userDTO) throws IdInvalidException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IdInvalidException("Username already exists");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IdInvalidException("Email already exists");
        }

        User user = UserMapper.mapToUser(userDTO);
        user.setActive(true);
        user.setVerified(false);
        user.setIsDeleted(false);

        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    public UserDTO login(String username, String password) throws IdInvalidException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new IdInvalidException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new IdInvalidException("Account is deactivated");
        }

        if (user.getIsDeleted()) {
            throw new IdInvalidException("Account is deleted");
        }

        return UserMapper.mapToUserDTO(user);
    }
}

