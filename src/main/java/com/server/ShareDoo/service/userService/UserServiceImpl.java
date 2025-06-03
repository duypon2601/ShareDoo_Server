package com.server.ShareDoo.service.userService;

import com.server.ShareDoo.dto.request.userRequest.CreateUserDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.dto.response.ResUserDTO;
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
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public UserDTO getUserById(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO getUserByUsername(String username) throws IdInvalidException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IdInvalidException("User not found with username: " + username));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public List<ResUserDTO> getUserAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::mapToUserDTO)
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());
    }
    @Override
    public ResUserDTO convertToResUserDTO(UserDTO user) {
        ResUserDTO res = new ResUserDTO();
        res.setUserId(user.getUserId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole());
        res.setActive(user.isActive());
        res.setVerified(user.isVerified());
        res.setDeleted(user.isDeleted());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setLastLoginAt(user.getLastLoginAt());

        return res;
    }

    @Override
    @Transactional
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
    @Transactional
    public Page<UserDTO> getDeletedUsers(Pageable pageable) {
        return userRepository.findAllDeletedUsers(pageable)
                .map(UserMapper::mapToUserDTO);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer userId, UserDTO userDTO) throws IdInvalidException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));

        // Validate email uniqueness if email is being changed
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IdInvalidException("Email already exists");
        }

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(existingUser);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        if (user.getIsDeleted()) {
            throw new IdInvalidException("User is already deleted");
        }

        userRepository.softDeleteUser(userId);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO restoreUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        if (!user.getIsDeleted()) {
            throw new IdInvalidException("User is not deleted");
        }

        userRepository.restoreUser(userId);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO activateUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        if (user.isActive()) {
            throw new IdInvalidException("User is already active");
        }

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO deactivateUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        if (!user.isActive()) {
            throw new IdInvalidException("User is already inactive");
        }

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO verifyUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        if (user.isVerified()) {
            throw new IdInvalidException("User is already verified");
        }

        user.setVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateLastLogin(Integer userId) throws IdInvalidException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User not found with id: " + userId));
        
        user.setLastLoginAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
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
