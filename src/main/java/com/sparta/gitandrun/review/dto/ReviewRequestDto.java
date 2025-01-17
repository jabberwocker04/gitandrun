package com.sparta.gitandrun.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequestDto {

    @Size(max = 500, message = "리뷰 내용은 최대 500자까지 입력할 수 있습니다.")
    private String reviewContent;

    @Min(value = 1, message = "평점은 최소 1점부터 입력 가능합니다.")
    @Max(value = 5, message = "평점은 최대 5점까지만 입력 가능합니다.")
    private Short reviewRating;
}