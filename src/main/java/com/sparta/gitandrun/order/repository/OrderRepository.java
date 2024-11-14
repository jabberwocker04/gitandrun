package com.sparta.gitandrun.order.repository;

import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.id = :orderId and o.isDeleted = false ")
    Optional<Order> findOrderById(@Param("orderId") Long orderId);

    //리뷰에서 orderId와 orderStatus 확인하기 위해 추가
    Optional<Order> findByIdAndOrderStatus(Long orderId, OrderStatus orderStatus);
  
    Page<Order> findByUser_UserIdAndIsDeletedFalse(Long userId, Pageable pageable);

}
