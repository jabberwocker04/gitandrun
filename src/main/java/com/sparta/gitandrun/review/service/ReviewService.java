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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    // 리뷰 작성
    @Transactional
    public void createReview(ReviewRequestDto requestDto, Long userId, Long orderId) {
        Order order = getOrder(orderId, userId);

        if (reviewRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("이미 리뷰가 작성된 주문입니다.");
        }

        // order-store 관계 맵핑 시 제거 예정
        List<OrderMenu> orderMenus = orderMenuRepository.findByOrderId(order.getId());
        if (orderMenus.isEmpty()) {
            throw new IllegalArgumentException("주문 메뉴가 존재하지 않습니다.");
        }

        //UUID storeId = order.getStore().getStoreId();
        UUID storeId = orderMenus.get(0).getMenu().getStore().getStoreId();

        Review review = new Review(requestDto, order.getUser(), storeId, order);
        reviewRepository.save(review);
    }

    //공통 - 가게별 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsByStore(UUID storeId, int page, int size, String sortBy) {
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByStoreId(storeId, pageable);
        return reviews.map(ReviewResponseDto::new);
    }

    // 사용자 - 본인 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsByUser(Long userId, int page, int size, String sortBy) {
        User user = getUser(userId);
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByUser(user, pageable);
        return reviews.map(ReviewResponseDto::new);
    }

    // 관리자 - 모든 리뷰 조회
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getAllReviews(int page, int size, String sortBy) {
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(ReviewResponseDto::new);
    }

    // 관리자 - 리뷰 아이디로 조회
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @Transactional(readOnly = true)
    public ReviewResponseDto getOneReview(UUID reviewId) {
        Review review = getReview(reviewId);
        return new ReviewResponseDto(review);
    }

    // 관리자 - 키워드로 리뷰 검색
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsByKeyword(String keyword, int page, int size, String sortBy) {
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByReviewContentContaining(keyword, pageable);
        return reviews.map(ReviewResponseDto::new);
    }

    //리뷰 수정
    @Transactional
    public void updateReview(UUID reviewId, ReviewRequestDto requestDto) {
        Review review = getReview(reviewId);

        if (requestDto.getReviewContent() != null && !requestDto.getReviewContent().isEmpty()) {
            review.setReviewContent(requestDto.getReviewContent());
        }

        if (requestDto.getReviewRating() != null) {
            review.setReviewRating(requestDto.getReviewRating());
        }
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));
        review.setDeleted(true);
    }

    //사용자 확인
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    //리뷰 확인
    private Review getReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    }

    // 주문 존재 여부 및 본인 주문 확인
    private Order getOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));
        if (!order.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 주문만 리뷰를 작성할 수 있습니다.");
        }
        if (!order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new IllegalArgumentException("완료된 주문만 리뷰 작성이 가능합니다.");
        }
        return order;
    }

    //페이지 처리 옵션
    private Pageable optionPageable(int page, int size, String sortBy) {
        // 기본 정렬 = createdAt, 정렬 추가 updatedAt
        String sortField = "updatedAt".equals(sortBy) ? "updatedAt" : "createdAt";
        return PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));
    }
}