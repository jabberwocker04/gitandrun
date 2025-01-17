package com.sparta.gitandrun.order.repository;

import com.sparta.gitandrun.order.entity.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
    @Query("select om " +
            "from OrderMenu om " +
            "join fetch om.menu " +
            "where om.order.id in :orderIds")
    List<OrderMenu> findByOrderIds(@Param("orderIds")List<UUID> orderIds);

    @Query("select om " +
            "from OrderMenu om " +
            "join fetch om.menu " +
            "where om.order.id = :orderId")
    List<OrderMenu> findByOrderId(@Param("orderId") UUID orderId);
}
