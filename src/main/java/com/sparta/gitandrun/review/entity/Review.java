package com.sparta.gitandrun.review.entity;

import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SoftDelete(columnName = "is_deleted")
@NoArgsConstructor
@Table(name = "p_review")
public class Review {

    @Id
    @Column(name = "review_id")
    private UUID reviewId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //회원별 조회

    @Column(name = "store_id", nullable = false)
    private UUID storeId; //가게별 조회

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order; //주문과 리뷰 연결

    @Column(name = "review_content", nullable = false, length = 500)
    private String reviewContent;

    @Column(name = "review_rating", nullable = false)
    private Short reviewRating;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, length = 30, updatable = false)
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", nullable = false, length = 30)
    protected String updatedBy;

    @Column(name = "is_deleted", insertable = false, updatable = false)
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 30)
    private String deletedBy;

    @PreRemove
    public void preRemove() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = String.valueOf(user.getUserId());
    }

    public Review(ReviewRequestDto requestDto, User user, UUID storeId, Order order) {
        this.user = user;
        this.storeId = storeId;
        this.order = order;
        this.reviewContent = requestDto.getReviewContent();
        this.reviewRating = requestDto.getReviewRating();
        this.createdBy = String.valueOf(user.getUserId());
        this.updatedBy = String.valueOf(user.getUserId());
    }
}
