package com.sparta.gitandrun.review.repository;

import com.sparta.gitandrun.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // 해당 주문에 리뷰가 이미 존재하는지 확인
    boolean existsByOrderId(Long orderId);

    // CUSTOMER, OWNER: 본인이 작성한 리뷰 조회
    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.isDeleted = false")
    Page<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // OWNER: 본인 가게의 사용자의 리뷰를 조회
    @Query("SELECT r FROM Review r WHERE r.store.storeId = :storeId AND r.user.userId = :userId AND r.isDeleted = false")
    Page<Review> findByStoreIdAndUserId(UUID storeId, Long userId, Pageable pageable);

    // CUSTOMER: 특정 가게의 리뷰를 조회
    @Query("SELECT r FROM Review r WHERE r.store.storeId = :storeId AND r.isDeleted = false")
    Page<Review> findByStoreId(@Param("storeId") UUID storeId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "WHERE (:keyword IS NULL OR LOWER(r.reviewContent) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:userId IS NULL OR r.user.userId = :userId) " +
            "AND (:reviewId IS NULL OR r.reviewId = :reviewId) " +
            "AND (:storeId IS NULL OR r.store.storeId = :storeId)")
    Page<Review> searchReviewsWithFilters(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            @Param("reviewId") UUID reviewId,
            @Param("storeId") UUID storeId,
            Pageable pageable);
}
