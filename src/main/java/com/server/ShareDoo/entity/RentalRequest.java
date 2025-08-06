package com.server.ShareDoo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rental_request")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "rental_id", nullable = false)
    Long rentalId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "status", nullable = false, length = 32)
    String status; // pending, confirmed, cancelled, etc.

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
