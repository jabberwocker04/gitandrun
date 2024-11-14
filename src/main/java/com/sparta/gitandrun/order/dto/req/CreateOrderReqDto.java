package com.sparta.gitandrun.order.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateOrderReqDto {
    private Long userId;
    private List<UUID> menuIds;
    private boolean type;
}
