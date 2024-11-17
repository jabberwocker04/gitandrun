package com.sparta.gitandrun.order.dto.res;

import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResOrderGetByIdDTO {

    private OrderDTO orderDTO;

    public static ResOrderGetByIdDTO of(Order order, List<OrderMenu> orderMenus) {
        return ResOrderGetByIdDTO.builder()
                .orderDTO(OrderDTO.from(order, orderMenus))
                .build();
    }



    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OrderDTO {
        private Long orderId;
        private String status;
        private String type;
        private LocalDateTime createdAt;
        private List<OrderMenuDTO> orderMenuDTOS;
        private int totalPrice;
        private StoreDTO storeDTO;

        private static OrderDTO from(Order order, List<OrderMenu> orderMenus) {

            List<OrderMenuDTO> orderMenuDTOS = OrderMenuDTO.from(orderMenus).get(order.getId());

            return OrderDTO.builder()
                    .orderId(order.getId())
                    .status(order.getOrderStatus().status)
                    .type(order.getOrderType().getType())
                    .createdAt(order.getCreatedAt())
                    .orderMenuDTOS(orderMenuDTOS)
                    .totalPrice(sumFrom(orderMenuDTOS))
                    .storeDTO(OrderDTO.StoreDTO.from(order.getStore()))
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
        private static class OrderMenuDTO {

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
            private UUID storeId;
            private String zipcode;
            private String address;
            private String addressDetail;

            private static StoreDTO from(Store store) {
                return StoreDTO.builder()
                        .storeId(store.getStoreId())
                        .zipcode(store.getAddress().getZipCode())
                        .address(store.getAddress().getAddress())
                        .addressDetail(store.getAddress().getAddressDetail())
                        .build();
            }
        }
    }
}
