package com.sparta.gitandrun.review.service;

import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.review.dto.ReviewResponseDto;
import com.sparta.gitandrun.review.entity.Review;
import com.sparta.gitandrun.review.repository.ReviewRepository;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.order.entity.OrderStatus;
import com.sparta.gitandrun.order.repository.OrderMenuRepository;
import com.sparta.gitandrun.order.repository.OrderRepository;
import com.sparta.gitandrun.user.entity.User;
import com.sparta.gitandrun.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    //리뷰 작성
    public Review createReview(ReviewRequestDto requestDto, Long userId, Long orderId) {
        // userId로 User 객체 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // orderId로 Order 객체 조회 및 완료된 주문인지 확인
        Order order = orderRepository.findByIdAndOrderStatus(orderId, OrderStatus.COMPLETED)
                .orElseThrow(() -> new IllegalArgumentException("완료된 주문만 리뷰 작성이 가능합니다."));

        // 해당 주문에 리뷰가 이미 존재하는지 확인
        if (reviewRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("이미 리뷰가 작성된 주문입니다.");
        }

        //orderId에 맞는 주문 메뉴 가져오기
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(orderId);

        //가게 정보 가져오기
        UUID storeId = orderMenus.get(0).getMenu().getStore().getStoreId();

        Review review = new Review(requestDto, user, storeId, order);
        return reviewRepository.save(review);
    }

    //리뷰 전체 조회
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    //리뷰 단일 조회
    public ReviewResponseDto getOneReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        return new ReviewResponseDto(review);
    }

    //리뷰 수정
    @Transactional
    public void updateReview(UUID reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        if (requestDto.getReviewContent() != null && !requestDto.getReviewContent().isEmpty()) {
            review.setReviewContent(requestDto.getReviewContent());
        }

        if (requestDto.getReviewRating() != null) {
            review.setReviewRating(requestDto.getReviewRating());
        }
        reviewRepository.save(review);
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        reviewRepository.delete(review);
    }
}