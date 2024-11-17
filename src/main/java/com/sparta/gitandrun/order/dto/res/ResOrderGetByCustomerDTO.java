package com.sparta.gitandrun.order.dto.res;

import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderGetByCustomerDTO {

    private OrderPage orderPage;

    public static ResOrderGetByCustomerDTO of(Page<Order> orderPage, List<OrderMenu> orderMenus) {
        return ResOrderGetByCustomerDTO.builder()
                .orderPage(new OrderPage(orderPage, orderMenus))
                .build();
    }

    @Getter
    public static class OrderPage extends PagedModel<OrderPage.OrderDTO> {

        private OrderPage(Page<Order> orderPage, List<OrderMenu> orderMenus) {
            super(
                    new PageImpl<>(
                            OrderDTO.from(orderPage.getContent(), orderMenus),
                            orderPage.getPageable(),
                            orderPage.getTotalElements()
                    )
            );
        }


        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrderDTO {

            private Long orderId;
            private String status;
            private String type;
            private List<OrderMenuDTO> orderMenuDTOS;
            private StoreDTO storeDTO;
            private int totalPrice;

            private static List<OrderDTO> from(List<Order> orders, List<OrderMenu> orderMenus) {
                return orders.stream()
                        .map(order -> from(order, orderMenus))
                        .toList();
            }

            private static OrderDTO from(Order order, List<OrderMenu> orderMenus) {

                List<OrderMenuDTO> orderMenuDTOS = OrderMenuDTO.from(orderMenus).get(order.getId());

                return OrderDTO.builder()
                        .orderId(order.getId())
                        .status(order.getOrderStatus().status)
                        .type(order.getOrderType().getType())
                        .orderMenuDTOS(orderMenuDTOS)
                        .totalPrice(sumFrom(orderMenuDTOS))
                        .storeDTO(StoreDTO.from(order.getStore()))
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

                private Long orderMenuId;
                private UUID menuId;
                private String menuName;
                private int menuPrice;
                private int count;

                private static Map<Long, List<OrderMenuDTO>> from(List<OrderMenu> orderMenus) {
                    return orderMenus.stream()
                            .collect(Collectors.groupingBy(
                                    orderMenu -> orderMenu.getOrder().getId(),
                                    Collectors.mapping(
                                            OrderMenuDTO::from,
                                            Collectors.toList()
                                    )
                            ));
                }

                private static OrderMenuDTO from(OrderMenu orderMenu) {
                    return OrderMenuDTO.builder()
                            .orderMenuId(orderMenu.getId())
                            .menuId(orderMenu.getMenu().getMenuId())
                            .menuName(orderMenu.getMenu().getMenuName())
                            .menuPrice(orderMenu.getOrderPrice())
                            .count(orderMenu.getOrderCount())
                            .build();
                }


            }

            @Getter
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            private static class StoreDTO {

                private String name;

                private static StoreDTO from (Store store) {
                    return StoreDTO.builder()
                            .name(store.getStoreName())
                            .build();
                }
            }
        }
    }
}
