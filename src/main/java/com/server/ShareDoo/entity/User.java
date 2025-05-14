package com.server.ShareDoo.entity;


import com.server.ShareDoo.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Column(name = "full_name")
    private String fullName;   // full_name
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    Role role;


    @Column(name="phone",nullable = false)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "is_verified")
    private boolean isVerified; // đã xác minh email/phone?

    @Column(name = "avatar_url")
    private String avatarUrl; // ảnh đại diện

    @Column(name = "is_active")
    private boolean isActive = true; // trạng thái hoạt động

    @Column(name = "is_deleted")
    private boolean isDeleted = false; // hỗ trợ xóa mềm

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}