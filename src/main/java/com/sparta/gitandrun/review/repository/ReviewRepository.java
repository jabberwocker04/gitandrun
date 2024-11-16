package com.sparta.gitandrun.review.repository;

import com.sparta.gitandrun.review.entity.Review;
import com.sparta.gitandrun.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // 해당 주문에 리뷰가 이미 존재하는지 확인
    boolean existsByOrderId(Long orderId);

    Page<Review> findByUser(User user, Pageable pageable);

    Page<Review> findByStoreId(UUID storeId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(r.reviewContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CONCAT(r.reviewId, '') LIKE CONCAT('%', :keyword, '%') OR " +
            "CONCAT(r.user.userId, '') LIKE CONCAT('%', :keyword, '%') OR " +
            "CONCAT(r.storeId, '') LIKE CONCAT('%', :keyword, '%'))")
    Page<Review> searchReviewsWithKeyword(@Param("keyword") String keyword, Pageable pageable);
}
