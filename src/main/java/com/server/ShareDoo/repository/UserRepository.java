package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Find by unique fields
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Search with pagination
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByEmailContaining(String email, Pageable pageable);
    Page<User> findByUsernameContainingAndEmailContaining(String username, String email, Pageable pageable);
    
    // Existence checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Soft delete operations
    @Query("SELECT u FROM User u WHERE u.isDeleted = true")
    Page<User> findAllDeletedUsers(Pageable pageable);
    
    @Modifying
    @Query("UPDATE User u SET u.isDeleted = false WHERE u.userId = :userId")
    void restoreUser(Integer userId);
    
    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.userId = :userId")
    void softDeleteUser(Integer userId);
    
    // Find by role
    List<User> findByRole(String role);
}