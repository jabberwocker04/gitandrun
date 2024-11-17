package com.sparta.gitandrun.order.entity;

import com.sparta.gitandrun.common.entity.BaseEntity;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    private boolean isDeleted;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "p_user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "p_store_id")
    private Store store;

    @OneToMany(mappedBy = "order", cascade = PERSIST, orphanRemoval = true)
    private List<OrderMenu> orderMenus = new ArrayList<>();

    @Builder
    public Order(User user, OrderStatus status, OrderType type, Store store) {
        this.user = user;
        this.orderStatus = status;
        this.orderType = type;
        this.store = store;
    }

    public void addOrderMenus(List<OrderMenu> orderMenus) {
        this.orderMenus.addAll(orderMenus);
        orderMenus.forEach(orderMenu -> orderMenu.updateOrder(this));
    }

    // == 생성 메서드 == //
    public static Order createOrder(User user, boolean type, List<OrderMenu> orderMenus) {
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .type(type ? OrderType.DELIVERY : OrderType.VISIT)
                .store(orderMenus.get(0).getMenu().getStore())
                .build();

        order.initAuditInfo(user);
        order.addOrderMenus(orderMenus);

        return order;
    }

    // == 주문 취소 메서드 == //
    public void cancelOrder() {
        if (Objects.equals(this.getOrderStatus().status, OrderStatus.CANCEL.status)) {
            throw new IllegalArgumentException("이미 취소가 된 주문입니다.");
        }
        this.orderStatus = OrderStatus.CANCEL;
    }

    // == 주문 거절 메서드 == //
    public void rejectOrder() {
        if (!Objects.equals(this.getOrderStatus().status, OrderStatus.PENDING.status)) {
            throw new IllegalArgumentException("해당 주문은 이미 취소, 완료 또는 거절된 상태입니다.");
        }
        this.orderStatus = OrderStatus.REJECT;
    }

    // == 주문상태 변경 메서드 == //
    public Order completeOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
        return this;
    }

    // == 조회 메서드 == //
    public int getTotalPrice() {
        return orderMenus.stream().mapToInt(OrderMenu::getOrderPrice).sum();
    }
}