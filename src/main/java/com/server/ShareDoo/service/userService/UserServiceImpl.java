package com.server.ShareDoo.service.userService;

import com.server.ShareDoo.dto.request.userRequest.CreateUserDTO;
import com.server.ShareDoo.dto.request.userRequest.UserDTO;
import com.server.ShareDoo.dto.response.ResCreateUserDTO;
import com.server.ShareDoo.dto.response.ResUserDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.mapper.UserMapper;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.util.error.IdInvalidException;
import com.server.ShareDoo.util.error.NotFoundException;
import com.server.ShareDoo.util.error.AuthHandlerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResCreateUserDTO createUser(CreateUserDTO createUserDTO) throws IdInvalidException {
        // Validate input
        if (!StringUtils.hasText(createUserDTO.getUsername())) {
            throw new IdInvalidException("Username is required");
        }
        if (!StringUtils.hasText(createUserDTO.getPassword())) {
            throw new IdInvalidException("Password is required");
        }
        if (!StringUtils.hasText(createUserDTO.getEmail())) {
            throw new IdInvalidException("Email is required");
        }
        if (!StringUtils.hasText(createUserDTO.getName())) {
            throw new IdInvalidException("Name is required");
        }
        if (!StringUtils.hasText(createUserDTO.getAddress())) {
            throw new IdInvalidException("Address is required");
        }

        // Validate password length
        if (createUserDTO.getPassword().length() < 6) {
            throw new IdInvalidException("Password must be at least 6 characters long");
        }

        // Validate email format
        if (!emailPattern.matcher(createUserDTO.getEmail()).matches()) {
            throw new IdInvalidException("Invalid email format");
        }

        // Validate name length
        if (createUserDTO.getName().length() < 2) {
            throw new IdInvalidException("Name must be at least 2 characters long");
        }

        // Validate address length
        if (createUserDTO.getAddress().length() < 5) {
            throw new IdInvalidException("Address must be at least 5 characters long");
        }

        // Check for existing username and email
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
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        return UserMapper.mapToUserDTO(user);
    }

    @Override
    @Transactional
    public UserDTO getUserByUsername(String username) throws IdInvalidException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
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
    public Page<ResUserDTO> searchUsers(String username, String email, Pageable pageable) {
        List<ResUserDTO> allUsers = getUserAll();
        
        // Filter users based on search criteria
        List<ResUserDTO> filteredUsers = allUsers.stream()
            .filter(user -> {
                boolean matchesUsername = username == null || 
                    user.getUsername().toLowerCase().contains(username.toLowerCase());
                boolean matchesEmail = email == null || 
                    user.getEmail().toLowerCase().contains(email.toLowerCase());
                return matchesUsername && matchesEmail;
            })
            .collect(Collectors.toList());

        // Convert to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredUsers.size());
        
        return new PageImpl<>(
            filteredUsers.subList(start, end),
            pageable,
            filteredUsers.size()
        );
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate email format if email is being changed
        if (!existingUser.getEmail().equals(userDTO.getEmail())) {
            if (!emailPattern.matcher(userDTO.getEmail()).matches()) {
                throw new IdInvalidException("Invalid email format");
            }
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IdInvalidException("Email already exists");
            }
        }

        // Validate name
        if (!StringUtils.hasText(userDTO.getName()) || userDTO.getName().length() < 2) {
            throw new IdInvalidException("Name must be at least 2 characters long");
        }

        // Validate address
        if (!StringUtils.hasText(userDTO.getAddress()) || userDTO.getAddress().length() < 5) {
            throw new IdInvalidException("Address must be at least 5 characters long");
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
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
