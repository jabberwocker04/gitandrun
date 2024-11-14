package com.sparta.gitandrun.order.controller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.order.dto.req.CreateOrderReqDto;
import com.sparta.gitandrun.order.dto.res.ResDto;
import com.sparta.gitandrun.order.dto.res.ResOrderGetByIdDTO;
import com.sparta.gitandrun.order.dto.res.ResOrderGetDTO;
import com.sparta.gitandrun.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /*
        주문생성 메서드
        1. @Secured("CUSTOMER") 추후 접근 권한 처리
        2. 매개변수로 User 추가할 것
    */
    @PostMapping
    public ResponseEntity<ApiResDto> createOrder(@RequestBody CreateOrderReqDto dto) {

        orderService.createOrder(dto);

        return ResponseEntity.ok().body(new ApiResDto("주문 완료", HttpStatus.OK.value()));
    }

    /*
       주문 전체 조회
       1. 유저 본인의 주문 내역을 조회할 수 있음.
       2. 추후, 주문 상태별 / 최신순 및 오래된 순 등 다양한 기준에 따라 정렬 및 조회 가능한 동적 쿼리 작성 예정
       3. @PathVariable 삭제하고 인증 객체를 받아 user 정보를 받은 예정
   */
    @GetMapping("/{userId}")
    public ResponseEntity<ResDto<ResOrderGetDTO>> readOrder(
            @PathVariable("userId")Long userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return orderService.getBy(userId, pageable);
    }

    /*
        주문 단일 및 상세 조회
    */
    @GetMapping("/{orderId}")
    public ResponseEntity<ResDto<ResOrderGetByIdDTO>> getById(@PathVariable("orderId")Long orderId) {
        return orderService.getBy(orderId);
    }


    /*
       주문취소 메서드
       1. @Secured("CUSTOMER, ADMIN") 추후 접근 권한 처리
       2. 매개변수로 User 추가할 것
   */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResDto> cancelOrder(@PathVariable("orderId") Long orderId) {

        orderService.cancelOrder(orderId);

        return ResponseEntity.ok().body(new ApiResDto("주문 취소 완료", HttpStatus.OK.value()));
    }

    /*
       주문거절 메서드
       1. @Secured("OWNER, MANAGER") 추후 접근 권한 처리
       2. 매개변수로 User 추가할 것
   */
    @PatchMapping("/{orderId}/reject")
    private ResponseEntity<ApiResDto> rejectOrder(@PathVariable("orderId") Long orderId) {

        orderService.rejectOrder(orderId);

        return ResponseEntity.ok().body(new ApiResDto("주문 거절 완료", HttpStatus.OK.value()));
    }
}
