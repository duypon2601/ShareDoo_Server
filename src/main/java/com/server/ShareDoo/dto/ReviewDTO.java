package com.server.ShareDoo.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String comment;
    private Long productId;
    private Long reviewerId;
    private int rating;
    private String imgUrl;
    private String createdAt;
    private String reviewerName;
}
