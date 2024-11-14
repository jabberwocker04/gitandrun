package com.sparta.gitandrun.order.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ResDto<T> {
    private Integer code;
    private String message;
    private T data;
}
