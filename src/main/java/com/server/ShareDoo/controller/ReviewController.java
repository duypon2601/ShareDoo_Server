package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.ReviewDTO;
import com.server.ShareDoo.entity.Rental;
import com.server.ShareDoo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor

public class ReviewController {
    private final ReviewService reviewService;
    private final com.server.ShareDoo.service.rentalService.RentalService rentalService;

    @PostMapping("")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO, @RequestParam Long orderCode) {
        // Truy vấn đơn hàng từ orderCode
        Rental rental = rentalService.findByOrderCode(orderCode);
        if (rental == null || rental.getProduct() == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng hoặc sản phẩm!");
        }
        reviewDTO.setProductId(rental.getProduct().getProductId());
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<ReviewDTO>> getByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewerId(reviewerId));
    }
}
