package com.sparta.gitandrun.review.controller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.review.dto.AdminReviewResponseDto;
import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.review.dto.UserReviewResponseDto;
import com.sparta.gitandrun.review.service.ReviewService;
import com.sparta.gitandrun.user.security.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성
    @Secured({"ROLE_CUSTOMER", "ROLE_OWNER"})
    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResDto> createReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReviewRequestDto requestDto,
            @PathVariable Long orderId) {
        Long userId = userDetails.getUser().getUserId();
        reviewService.createReview(requestDto, userId, orderId);
        return ResponseEntity.ok().body(new ApiResDto("리뷰 작성 완료", HttpStatus.OK.value()));
    }

    // OWNER: 본인 가게 리뷰만 조회
    @Secured("ROLE_OWNER")
    @GetMapping("/owner/{storeId}")
    public ApiResDto getOwnerReviewsByStore(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID storeId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Long userId = userDetails.getUser().getUserId();
        Page<UserReviewResponseDto> reviews = reviewService.getOwnerReviewsByStore(userId, storeId, page, size, sortBy);
        return new ApiResDto("리뷰 조회 성공", 200, reviews);
    }

    // 모든 가게 리뷰 조회 (OWNER 제외)
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_CUSTOMER"})
    @GetMapping("/store/{storeId}")
    public ApiResDto getCustomerReviewsByStore(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<UserReviewResponseDto> reviews = reviewService.getCustomerReviewsByStore(storeId, page, size, sortBy);
        return new ApiResDto("리뷰 조회 성공", 200, reviews);
    }

    // CUSTOEMR, OWNER - 본인이 작성한 리뷰 조회
    @Secured({"ROLE_CUSTOMER", "ROLE_OWNER"})
    @GetMapping("/myReviews")
    public ResponseEntity<ApiResDto> getMyReviewsByUserId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Long userId = userDetails.getUser().getUserId();
        Page<UserReviewResponseDto> reviews = reviewService.getMyReviewsByUserId(userId, page, size, sortBy);
        return ResponseEntity.ok().body(new ApiResDto("본인 리뷰 조회 성공", HttpStatus.OK.value(), reviews));
    }

    // 관리자 - 모든 리뷰 검색 (reviewContent, userId, reviewId, storeId)
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @GetMapping("/admin")
    public ApiResDto getReviewsByFilters(
            @RequestParam(required = false) String keyword,    // 키워드 검색
            @RequestParam(required = false) Long userId,      // userId로 검색
            @RequestParam(required = false) UUID reviewId,    // reviewId로 검색
            @RequestParam(required = false) UUID storeId,     // storeId로 검색
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<AdminReviewResponseDto> reviews = reviewService.searchReviewsWithFilters(
                keyword, userId, reviewId, storeId, page, size, sortBy);
        return new ApiResDto("리뷰 조회 성공", 200, reviews);
    }

    //리뷰 수정 - 완료
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResDto> updateReview(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID reviewId,
            @RequestBody ReviewRequestDto requestDto) {
        reviewService.updateReview(reviewId, userDetails, requestDto);
        return ResponseEntity.ok().body(new ApiResDto("리뷰 수정 완료", HttpStatus.OK.value()));
    }

    //리뷰 삭제
    @DeleteMapping("{reviewId}")
    public ResponseEntity<ApiResDto> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.deleteReview(reviewId, userDetails);
        return ResponseEntity.ok().body(new ApiResDto("리뷰 삭제 완료", HttpStatus.OK.value()));
    }
}

