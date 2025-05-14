package com.server.ShareDoo.service.userService;

import com.server.ShareDoo.dto.request.UserDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.mapper.UserMapper;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.util.error.IdInvalidException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) throws IdInvalidException {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IdInvalidException("Username " + userDTO.getUsername() + " đã tồn tại, vui lòng sử dụng email khác.");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = UserMapper.mapToUser(userDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Integer userId) throws IdInvalidException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserMapper.mapToUserDTO(user.get());
        } else {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }
    }

    @Override
    public List<UserDTO> getUserAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO updateUser, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User " + userId + " not found"));
        
        user.setFullName(updateUser.getFullName());
        user.setEmail(updateUser.getEmail());
        user.setUsername(updateUser.getUsername());
        user.setRole(updateUser.getRole());
        user.setPhone(updateUser.getPhone());
        user.setAddress(updateUser.getAddress());
        user.setAvatarUrl(updateUser.getAvatarUrl());
        
        User updatedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(updatedUser);
    }

    @Override
    public void deleteUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User với id = " + userId + " không tồn tại"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserDTO handleGetUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public boolean isUsernameExist(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneExist(String phone) {
        return this.userRepository.existsByPhone(phone);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phone));
    }
}
