package com.server.ShareDoo.service.impl;

import com.server.ShareDoo.dto.ReviewDTO;
import com.server.ShareDoo.entity.Review;
import com.server.ShareDoo.repository.ReviewRepository;
import com.server.ShareDoo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final com.server.ShareDoo.repository.RentalRepository rentalRepository;
    private final com.server.ShareDoo.repository.UserRepository userRepository;

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        if (reviewDTO.getReviewerId() == null) {
            throw new RuntimeException("Thiếu thông tin người đánh giá (reviewerId)!");
        }
        // Kiểm tra trạng thái đơn hàng
        boolean canReview = rentalRepository.existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(
            reviewDTO.getReviewerId().intValue(), reviewDTO.getProductId(), "completed"
        );
        if (!canReview) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sau khi đã trả hàng thành công!");
        }
        // Kiểm tra đã review chưa
        if (reviewRepository.findByProductIdAndReviewerId(reviewDTO.getProductId(), reviewDTO.getReviewerId()).isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này!");
        }
        Review review = new Review();
        BeanUtils.copyProperties(reviewDTO, review);
        review = reviewRepository.save(review);
        return toDTO(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO> getReviewsByReviewerId(Long reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        BeanUtils.copyProperties(review, dto);
        if (review.getCreatedAt() != null) {
            dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (review.getReviewerId() != null) {
            userRepository.findById(review.getReviewerId().intValue())
                .ifPresent(user -> dto.setReviewerName(user.getName() != null ? user.getName() : user.getUsername()));
        }
        return dto;
    }
}
