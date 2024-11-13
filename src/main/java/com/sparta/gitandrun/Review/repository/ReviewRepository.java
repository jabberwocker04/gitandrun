package com.sparta.gitandrun.Review.repository;

import com.sparta.gitandrun.Review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // 해당 주문에 리뷰가 이미 존재하는지 확인
    boolean existsByOrderId(Long orderId);
}
