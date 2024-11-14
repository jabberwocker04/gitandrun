package com.sparta.gitandrun.review.dto;

import com.sparta.gitandrun.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {

    private UUID reviewId;
    private Long userId;
    private Long orderId;
    private UUID storeId;
    private String reviewContent;
    private Short reviewRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.userId = review.getUser().getUserId();
        this.orderId = review.getOrder().getId();
        this.storeId = review.getStoreId();
        this.reviewContent = review.getReviewContent();
        this.reviewRating = review.getReviewRating();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
}
