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
    Long id;

    Long rentalId;
    Long ownerId;
    String status; // pending, confirmed, cancelled, etc.

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
