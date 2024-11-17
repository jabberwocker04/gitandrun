package com.sparta.gitandrun.review.service;

import com.sparta.gitandrun.review.dto.AdminReviewResponseDto;
import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.review.dto.UserReviewResponseDto;
import com.sparta.gitandrun.review.entity.Review;
import com.sparta.gitandrun.review.repository.ReviewRepository;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderStatus;
import com.sparta.gitandrun.order.repository.OrderRepository;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.store.repository.StoreRepository;
import com.sparta.gitandrun.user.entity.Role;
import com.sparta.gitandrun.user.security.UserDetailsImpl;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    // 리뷰 작성
    @Transactional
    public void createReview(ReviewRequestDto requestDto, Long userId, Long orderId) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 주문 소유 및 상태 확인
        validateOrder(order, userId);

        // 3. 중복 리뷰 확인
        if (reviewRepository.existsByOrder(order)) {
            throw new IllegalStateException("이미 리뷰가 작성된 주문입니다.");
        }

        Review review = new Review(requestDto, order.getUser(), order.getStore(), order);
        reviewRepository.save(review);
    }

    // OWNER: 본인 가게 리뷰 조회
    @Transactional(readOnly = true)
    public Page<UserReviewResponseDto> getOwnerReviewsByStore(Long userId, UUID storeId, int page, int size, String sortBy) {
        List<Store> stores = storeRepository.findByUser_UserId(userId);
        boolean isStoreOwner = stores.stream().anyMatch(store -> store.getStoreId().equals(storeId));

        if (!isStoreOwner) {
            throw new IllegalArgumentException("본인 가게가 아닙니다.");
        }

        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByStoreIdAndUserId(storeId, userId, pageable);
        reviewEmpty(reviews);
        return reviews.map(UserReviewResponseDto::new);
    }

    // CUSTOMER: 모든 가게 리뷰 조회
    @Transactional(readOnly = true)
    public Page<UserReviewResponseDto> getCustomerReviewsByStore(UUID storeId, int page, int size, String sortBy) {
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByStoreId(storeId, pageable);
        reviewEmpty(reviews);
        return reviews.map(UserReviewResponseDto::new);
    }

    // CUSTOEMR, OWNER - 본인이 작성한 리뷰 조회
    @Transactional(readOnly = true)
    public Page<UserReviewResponseDto> getMyReviewsByUserId(Long userId, int page, int size, String sortBy) {
        Pageable pageable = optionPageable(page, size, sortBy);
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);
        reviewEmpty(reviews);
        return reviews.map(UserReviewResponseDto::new);
    }

    // 관리자 - 모든 리뷰 검색 (키워드)
    @Transactional(readOnly = true)
    public Page<AdminReviewResponseDto> searchReviewsWithKeyword(
            String keyword, int page, int size, String sortBy) {
            Pageable pageable = optionPageable(page, size, sortBy);

        // keyword가 null이거나 공백인 경우 전체 조회
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<Review> allReviews = reviewRepository.findAll(pageable);
            reviewEmpty(allReviews);
            return allReviews.map(AdminReviewResponseDto::new);
        }

        Page<Review> reviews = reviewRepository.searchReviewsWithKeyword(keyword, pageable);
        reviewEmpty(reviews);
        return reviews.map(AdminReviewResponseDto::new);
    }

    //리뷰 수정
    @Transactional
    public void updateReview(UUID reviewId, UserDetailsImpl userDetails, ReviewRequestDto requestDto) {
        Review review = getReview(reviewId);
        Long userId = userDetails.getUser().getUserId();
        Role role = userDetails.getUser().getRole();

        checkPermission(review, userId, role);

        if (requestDto.getReviewContent() != null && !requestDto.getReviewContent().isEmpty()) {
            review.setReviewContent(requestDto.getReviewContent());
        }
        if (requestDto.getReviewRating() != null) {
            review.setReviewRating(requestDto.getReviewRating());
        }
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(UUID reviewId, UserDetailsImpl userDetails) {
        Review review = getReview(reviewId);
        Long userId = userDetails.getUser().getUserId();
        Role role = userDetails.getUser().getRole();
        checkPermission(review, userId, role);
        review.setDeleted(true);
    }

    //----------------------------------------------------------------

    //리뷰 확인
    private Review getReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    }

    // 주문 존재 여부 및 본인 주문 확인
    private void validateOrder(Order order, Long userId) {
        // 주문 소유자 확인
        if (!order.getUser().getUserId().equals(userId)) {
            throw new SecurityException("해당 주문에 대한 권한이 없습니다.");
        }

        // 주문 상태 확인
        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("리뷰를 작성할 수 있는 상태의 주문이 아닙니다.");
        }
    }

    // 리뷰가 비어있는지 확인
    private void reviewEmpty(Page<Review> reviews) {
        if (reviews.isEmpty()) {
            throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
        }
    }

    //페이지 처리 옵션
    private Pageable optionPageable(int page, int size, String sortBy) {
        String sortField = "updatedAt".equals(sortBy) ? "updatedAt" : "createdAt";

        if (size != 10 && size != 30 && size != 50) {
            throw new IllegalArgumentException("페이지 크기는 10, 30, 50 중 하나로 설정해야 합니다.");
        }
        return PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));
    }

    // CUSTOMER 또는 OWNER 권한 확인
    private void checkPermission(Review review, Long userId, Role role) {
        if (role.equals(Role.CUSTOMER) || role.equals(Role.OWNER)) {
            if (!review.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("본인의 리뷰만 가능합니다.");
            }
        }
    }
}