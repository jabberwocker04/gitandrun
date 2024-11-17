package com.sparta.gitandrun.review.dto;

import com.sparta.gitandrun.review.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminReviewResponseDto {

    private UUID reviewId;
    private Long userId;
    private Long orderId;
    private UUID storeId;
    private String reviewContent;
    private Short reviewRating;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public AdminReviewResponseDto(Review review) {
        this.reviewId = review.getReviewId();
        this.userId = review.getUser().getUserId();
        this.orderId = review.getOrder().getId();
        this.storeId = review.getStore().getStoreId();
        this.reviewContent = review.getReviewContent();
        this.reviewRating = review.getReviewRating();
        this.createdBy = review.getCreatedBy();
        this.createdAt = review.getCreatedAt();
        this.updatedBy = review.getUpdatedBy();
        this.updatedAt = review.getUpdatedAt();
        this.isDeleted = review.isDeleted();
        this.deletedBy = review.getDeletedBy();
        this.deletedAt = review.getDeletedAt();
    }
}
