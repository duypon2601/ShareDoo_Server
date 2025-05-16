package com.server.ShareDoo.service.userService;

import com.server.ShareDoo.dto.request.userRequest.CreateUserDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.mapper.UserMapper;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.util.error.IdInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ResCreateUserDTO createUser(CreateUserDTO createUserDTO) throws IdInvalidException {
        if (userRepository.findByUsername(createUserDTO.getUsername()).isPresent()) {
            throw new IdInvalidException("Username already exists");
        }
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new IdInvalidException("Email already exists");
        }
        
        User user = new User();
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        user.setAddress(createUserDTO.getAddress());
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(createUserDTO.getPassword());
        user.setRole(createUserDTO.getRole());
        
        // Set default values
        user.setActive(true);
        user.setVerified(false);
        user.setIsDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return UserMapper.mapToResCreateUserDTO(UserMapper.mapToUserDTO(savedUser));
    }

    @Override
    public UserDTO getUserById(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) throws IdInvalidException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::mapToUserDTO);
    }

    @Override
    public Page<UserDTO> searchUsers(String username, String email, Pageable pageable) {
        if (username != null && email != null) {
            return userRepository.findByUsernameContainingAndEmailContaining(username, email, pageable)
                    .map(UserMapper::mapToUserDTO);
        } else if (username != null) {
            return userRepository.findByUsernameContaining(username, pageable)
                    .map(UserMapper::mapToUserDTO);
        } else if (email != null) {
            return userRepository.findByEmailContaining(email, pageable)
                    .map(UserMapper::mapToUserDTO);
        }
        return getAllUsers(pageable);
    }

    @Override
    public Page<UserDTO> getDeletedUsers(Pageable pageable) {
        return userRepository.findAllDeletedUsers(pageable)
                .map(UserMapper::mapToUserDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer userId, UserDTO userDTO) throws IdInvalidException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return UserMapper.mapToUserDTO(userRepository.save(existingUser));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        userRepository.softDeleteUser(userId);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO restoreUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        userRepository.restoreUser(userId);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO activateUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO deactivateUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO verifyUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    @Override
    public UserDTO updateLastLogin(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        user.setLastLoginAt(LocalDateTime.now());
        return UserMapper.mapToUserDTO(userRepository.save(user));
    }

    @Override
    public boolean isUsernameExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(UserDTO userDTO) {
        return UserMapper.mapToResCreateUserDTO(userDTO);
    }

}
