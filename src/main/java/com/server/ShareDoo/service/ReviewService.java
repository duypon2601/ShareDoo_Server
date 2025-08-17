package com.server.ShareDoo.service;

import com.server.ShareDoo.dto.ReviewDTO;
import com.server.ShareDoo.entity.Review;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);
    List<ReviewDTO> getReviewsByProductId(Long productId);
    List<ReviewDTO> getReviewsByReviewerId(Long reviewerId);
}
