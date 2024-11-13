package com.sparta.gitandrun.order.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderGetDTO {

    private OrderDTO orderDTO;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDTO {

        private Long orderId;
        private String status;
        private String type;
        private List<OrderMenuDTO> orderMenuDTOS;
        private int totalPrice;

        public static List<OrderDTO> from(List<Order> orders, List<OrderMenu> orderMenus) {
            return orders.stream()
                    .map(order -> from(order, orderMenus))
                    .toList();
        }

        public static OrderDTO from(Order order, List<OrderMenu> orderMenus) {

            List<OrderMenuDTO> orderMenuDTOS = OrderMenuDTO.from(orderMenus).get(order.getId());

            return OrderDTO.builder()
                    .orderId(order.getId())
                    .status(order.getOrderStatus().status)
                    .type(order.getOrderType().getType())
                    .orderMenuDTOS(orderMenuDTOS)
                    .totalPrice(sumFrom(orderMenuDTOS))
                    .build();
        }

        private static int sumFrom(List<OrderMenuDTO> orderMenuDTOS) {
            return orderMenuDTOS.stream()
                    .mapToInt(OrderMenuDTO::getMenuPrice)
                    .sum();
        }

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrderMenuDTO {

            @JsonIgnore
            private Long orderId;
            private Long orderMenuId;
            private UUID menuId;
            private String menuName;
            private int menuPrice;
            private int count;

            public static Map<Long, List<OrderMenuDTO>> from(List<OrderMenu> orderMenus) {
                List<OrderMenuDTO> list = getOrderMenuDTOS(orderMenus);

                return list.stream()
                        .collect(Collectors.groupingBy(orderMenuDTO -> orderMenuDTO.orderId));
            }

            private static List<OrderMenuDTO> getOrderMenuDTOS(List<OrderMenu> orderMenus) {
                return orderMenus.stream()
                        .map(OrderMenuDTO::from)
                        .toList();
            }

            public static OrderMenuDTO from(OrderMenu orderMenu) {
                return OrderMenuDTO.builder()
                        .orderId(orderMenu.getOrder().getId())
                        .orderMenuId(orderMenu.getId())
                        .menuId(orderMenu.getMenu().getMenuId())
                        .menuName(orderMenu.getMenu().getMenuName())
                        .menuPrice(orderMenu.getOrderPrice())
                        .count(orderMenu.getOrderCount())
                        .build();
            }
        }
    }
}
