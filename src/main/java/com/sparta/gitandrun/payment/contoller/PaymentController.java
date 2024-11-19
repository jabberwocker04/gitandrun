package com.sparta.gitandrun.payment.contoller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.order.dto.res.ResDto;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByCustomerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByManagerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByOwnerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentPostDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByIdDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByManagerDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByOwnerDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByUserIdDTO;
import com.sparta.gitandrun.payment.service.PaymentService;
import com.sparta.gitandrun.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "Payment-Controller")
public class PaymentController {

    private final PaymentService paymentService;

    /*
        결제 생성
    */
    @Secured("ROLE_CUSTOMER")
    @PostMapping("/customer/payment")
    public ResponseEntity<ApiResDto> createPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestBody ReqPaymentPostDTO dto) {

        paymentService.createPayment(userDetails.getUser(), dto);

        return ResponseEntity.ok().body(new ApiResDto("결제 성공", HttpStatus.OK.value()));
    }


    /*
        본인 결제 조회
    */
    @Secured("ROLE_CUSTOMER")
    @PostMapping("/customer/search/payments")
    public ResponseEntity<ResDto<ResPaymentGetByUserIdDTO>> readPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                        @RequestBody ReqPaymentCondByCustomerDTO cond,
                                                                        @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return paymentService.getByCustomer(userDetails.getUser(), cond, pageable);
    }

    /*
        본인 가게 주문 조회
        - 사장 권한의 유저가 본인 가게의 전체 주문 내역을 조회할 수 있음.
    */
    @Secured("ROLE_OWNER")
    @PostMapping("/owner/search/payments")
    public ResponseEntity<ResDto<ResPaymentGetByOwnerDTO>> readPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestBody ReqPaymentCondByOwnerDTO cond,
                                                                       @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return paymentService.getByOwner(userDetails.getUser(), cond, pageable);
    }

//    @Secured("ROLE_OWNER")
//    @PostMapping("/owner/search/payments")
//    public


    /*
        MANAGER, ADMIN 결제 목록 전체 조회
    */
    @Secured({"ROLE_MANAGER", "ROLE_ADMIN"})
    @PostMapping("/manager/search/payments")
    public ResponseEntity<ResDto<ResPaymentGetByManagerDTO>> readPayment(@RequestBody ReqPaymentCondByManagerDTO cond,
                                                                         @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return paymentService.getByManager(cond, pageable);
    }


    /*
        결제 상세 조회
    */
    @PostMapping("/payment/{paymentId}")
    public ResponseEntity<ResDto<ResPaymentGetByIdDTO>> readPayment(@PathVariable("paymentId") UUID paymentId) {
        return paymentService.getBy(paymentId);
    }


    /*
       CUSTOMER, MANAGER 결제 취소
    */
    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER"})
    @PatchMapping("/payment/{paymentId}")
    public ResponseEntity<ApiResDto> cancelPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable("paymentId") UUID paymentId) {

        paymentService.cancelPayment(userDetails.getUser(), paymentId);

        return ResponseEntity.ok().body(new ApiResDto("취소 성공", HttpStatus.OK.value()));
    }


    /*
      ADMIN 결제 삭제
    */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/payment/{paymentId}")
    public ResponseEntity<ApiResDto> deletePayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable("paymentId") UUID paymentId) {

        paymentService.deletePayment(userDetails.getUser(), paymentId);

        return ResponseEntity.ok().body(new ApiResDto("취소 성공", HttpStatus.OK.value()));
    }
}
