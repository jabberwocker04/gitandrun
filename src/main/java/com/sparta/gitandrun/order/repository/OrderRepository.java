package com.sparta.gitandrun.order.repository;

import com.sparta.gitandrun.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndIsDeletedFalse(Long orderId);

    Optional<Order> findByIdAndUser_UserId(Long orderId, Long userId);

    Page<Order> findByUser_UserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    @Query("select o from Order o " +
            "join fetch o.user " +
            "where o.id in :orderIds " +
            "and o.isDeleted = false ")
    Page<Order> findByIdInAndIsDeletedFalse(@Param("orderIds") List<Long> orderIds, Pageable pageable);

    Optional<Order> findByIdAndStore_User_UserId(Long orderId, Long userId);
}
