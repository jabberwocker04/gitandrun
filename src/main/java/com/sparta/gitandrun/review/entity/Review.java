package com.sparta.gitandrun.review.entity;

import com.sparta.gitandrun.common.entity.BaseEntity;
import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_review")
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    private UUID reviewId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //회원별 조회

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; //가게별 조회

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order; //주문과 리뷰 연결

    @Column(name = "review_content", nullable = false, length = 500)
    private String reviewContent;

    @Column(name = "review_rating", nullable = false)
    private Short reviewRating;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Review(ReviewRequestDto requestDto, User user, Store store, Order order) {
        this.user = user;
        this.store = store;
        this.order = order;
        this.reviewContent = requestDto.getReviewContent();
        this.reviewRating = requestDto.getReviewRating();
        initAuditInfo(user);
    }
}
