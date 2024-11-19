package com.sparta.gitandrun.payment.service;

import com.sparta.gitandrun.order.dto.res.ResDto;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.order.repository.OrderMenuRepository;
import com.sparta.gitandrun.order.repository.OrderRepository;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByManagerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByCustomerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentCondByOwnerDTO;
import com.sparta.gitandrun.payment.dto.req.ReqPaymentPostDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByIdDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByManagerDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByOwnerDTO;
import com.sparta.gitandrun.payment.dto.res.ResPaymentGetByUserIdDTO;
import com.sparta.gitandrun.payment.entity.Payment;
import com.sparta.gitandrun.payment.repository.PaymentRepository;
import com.sparta.gitandrun.user.entity.Role;
import com.sparta.gitandrun.user.entity.User;
import com.sparta.gitandrun.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    /*
        결제 생성
    */
    @Transactional
    public void createPayment(User user, ReqPaymentPostDTO dto) {

        Order order = getOrder(user, dto);

        Payment payment = Payment.createPayment(dto.getOrderInfo().getPrice(), order, user);

        paymentRepository.save(payment);
    }

    /*
        고객 결제 목록 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResPaymentGetByUserIdDTO>> getByCustomer(User user, ReqPaymentCondByCustomerDTO cond, Pageable pageable) {

        Page<Payment> findPaymentPage = paymentRepository.findMyPaymentsWithConditions(user.getUserId(), cond, pageable);

        return new ResponseEntity<>(
                ResDto.<ResPaymentGetByUserIdDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("결제 목록 조회에 성공했습니다.")
                        .data(ResPaymentGetByUserIdDTO.of(findPaymentPage))
                        .build(),
                HttpStatus.OK
        );
    }

    /*
        사장 본인 가게 결제 목록 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResPaymentGetByOwnerDTO>> getByOwner(User user, ReqPaymentCondByOwnerDTO cond, Pageable pageable) {

        Page<Payment> findPaymentPage = paymentRepository.findStorePaymentsWithConditions(user.getUserId(), cond, pageable);

        return new ResponseEntity<>(
                ResDto.<ResPaymentGetByOwnerDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("결제 목록 조회에 성공했습니다.")
                        .data(ResPaymentGetByOwnerDTO.of(findPaymentPage))
                        .build(),
                HttpStatus.OK
        );
    }

    /*
        MANAGER, ADMIN 결제 목록 전체 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResPaymentGetByManagerDTO>> getByManager(ReqPaymentCondByManagerDTO cond, Pageable pageable) {

        Page<Payment> findPaymentPage = paymentRepository.findAllPaymentsWithConditions(cond, pageable);

        return new ResponseEntity<>(
                ResDto.<ResPaymentGetByManagerDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("결제 목록 조회에 성공했습니다.")
                        .data(ResPaymentGetByManagerDTO.of(findPaymentPage))
                        .build(),
                HttpStatus.OK
        );
    }


    /*
         결제 상세 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResPaymentGetByIdDTO>> getBy(UUID paymentId) {

        Payment findPayment = getPaymentBy(paymentId);

        List<OrderMenu> findOrderMenus = orderMenuRepository.findByOrderId(findPayment.getOrder().getId());

        return new ResponseEntity<>(
                ResDto.<ResPaymentGetByIdDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("결제 상세 조회에 성공했습니다.")
                        .data(ResPaymentGetByIdDTO.of(findPayment, findPayment.getOrder(), findOrderMenus))
                        .build(),
                HttpStatus.OK
        );
    }


    /*
        MANAGER, ADMIN 결제 취소
    */
    @Transactional
    public void cancelPayment(User user, UUID paymentId) {

        Payment payment =
                user.getRole() == Role.CUSTOMER
                        ? getPayment(paymentId, user.getUserId())
                        : getPayment(paymentId);

        payment.cancelPayment(user);
    }

    /*
        ADMIN 결제 삭제
    */
    @Transactional
    public void deletePayment(User user, UUID paymentId) {
        getPayment(paymentId).deletePayment(user);
    }


    private Payment getPaymentBy(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private Payment getPayment(UUID paymentId) {
        return paymentRepository.findPaidPaymentById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private Payment getPayment(UUID paymentId, Long userId) {
        return paymentRepository.findPaidPaymentByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private Order getOrder(User user, ReqPaymentPostDTO dto) {
        return orderRepository.findByIdAndUser_UserId(dto.getOrderInfo().getOrderId(), user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("접근 권한이 없습니다."));
    }

}
