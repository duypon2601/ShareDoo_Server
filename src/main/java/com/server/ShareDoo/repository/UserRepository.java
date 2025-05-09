package com.server.ShareDoo.repository;


import com.server.ShareDoo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

//    @Query("SELECT u FROM User u JOIN u.payments p WHERE p.payment_date BETWEEN :startDate AND :endDate")
//    List<User> findUsersByPaymentDates(String startDate, String endDate);
}