package com.sparta.gitandrun.payment.contoller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentPostDTO;
import com.sparta.gitandrun.payment.service.PaymentService;
import com.sparta.gitandrun.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
@Slf4j(topic = "Payment-Controller")
public class PaymentController {

    private final PaymentService paymentService;

    @Secured("ROLE_CUSTOMER")
    @PostMapping
    public ResponseEntity<ApiResDto> createPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @RequestBody ReqPaymentPostDTO dto) {

        paymentService.createPayment(userDetails.getUser(), dto);

        return ResponseEntity.ok().body(new ApiResDto("결제 성공", HttpStatus.OK.value()));
    }

    @Secured({"ROLE_CUSTOMER", "ROLE_MANAGER"})
    @PatchMapping("/{paymentId}")
    public ResponseEntity<ApiResDto> cancelPayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable("paymentId") Long paymentId) {

        paymentService.cancelPayment(userDetails.getUser(), paymentId);

        return ResponseEntity.ok().body(new ApiResDto("취소 성공", HttpStatus.OK.value()));
    }
}