package com.sparta.gitandrun.review.controller;

import com.sparta.gitandrun.review.dto.ReviewRequestDto;
import com.sparta.gitandrun.review.dto.ReviewResponseDto;
import com.sparta.gitandrun.review.entity.Review;
import com.sparta.gitandrun.review.service.ReviewService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto requestDto,
            @RequestParam Long userId,
            @RequestParam Long orderId) {
        Review review = reviewService.createReview(requestDto, userId, orderId);
        return ResponseEntity.ok(new ReviewResponseDto(review));
    }

    //리뷰 전체 조회
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    //리뷰 단일 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getOneReview(@PathVariable UUID reviewId) {
        ReviewResponseDto review = reviewService.getOneReview(reviewId);
        return ResponseEntity.ok(review);
    }

    //리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable UUID reviewId,
                                               @RequestBody ReviewRequestDto requestDto) {
        reviewService.updateReview(reviewId, requestDto);
        return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
    }

    //리뷰 삭제
    @DeleteMapping("{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}

