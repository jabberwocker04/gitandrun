package com.sparta.gitandrun.order.service;


import com.sparta.gitandrun.menu.entity.Menu;
import com.sparta.gitandrun.menu.repository.MenuRepository;
import com.sparta.gitandrun.order.dto.req.ReqOrderCondByCustomerDTO;
import com.sparta.gitandrun.order.dto.req.ReqOrderCondByOwnerDTO;
import com.sparta.gitandrun.order.dto.req.ReqOrderPostDTO;
import com.sparta.gitandrun.order.dto.res.ResDto;
import com.sparta.gitandrun.order.dto.res.ResOrderGetByCustomerDTO;
import com.sparta.gitandrun.order.dto.res.ResOrderGetByIdDTO;
import com.sparta.gitandrun.order.dto.res.ResOrderGetByOwnerDTO;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.order.repository.OrderMenuRepository;
import com.sparta.gitandrun.order.repository.OrderRepository;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    // 주문 생성
    @Transactional
    public void createOrder(User user, ReqOrderPostDTO dto) {
        /*
            유저 조회
        */
        User findUser = getUserById(user.getUserId());

        /*
            주문 목록 생성
        */
        List<OrderMenu> orderMenus = getOrderMenusByUserAndStoreId(dto);
        /*
            주문 생성
        */
        Order order = Order.createOrder(findUser, dto.getType().isType(), orderMenus);

        /*
            주문 저장
        */
        orderRepository.save(order);
    }


    /*
        Customer 본인 주문 내역 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResOrderGetByCustomerDTO>> readByCustomer(User user, ReqOrderCondByCustomerDTO cond, Pageable pageable) {
        /*
            주문 조회 : userId 를 기준으로
        */
        Page<Order> findOrderPage = orderRepository.findMyOrderListWithConditions(user.getUserId(), cond, pageable);

        /*
            주문 목록 조회 : 앞서 구한 order 의 id 를 기준으로
        */
        List<OrderMenu> orderMenus = getOrderMenusByOrderIds(getIdsByOrders(findOrderPage));

        return new ResponseEntity<>(
                ResDto.<ResOrderGetByCustomerDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("주문 조회에 성공했습니다.")
                        .data(ResOrderGetByCustomerDTO.of(findOrderPage, orderMenus))
                        .build(),
                HttpStatus.OK
        );
    }

    /*
        Owner 본인 가게 주문 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResOrderGetByOwnerDTO>> readByOwner(User user, ReqOrderCondByOwnerDTO cond, Pageable pageable) {

        Page<Order> findOrderPage = orderRepository.findOwnerOrderListWithConditions(user.getUserId(), cond, pageable);

        List<OrderMenu> findOrderMenus = getOrderMenusByOrderIds(getIdsByOrders(findOrderPage));

        return new ResponseEntity<>(
                ResDto.<ResOrderGetByOwnerDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("주문 조회에 성공했습니다.")
                        .data(ResOrderGetByOwnerDTO.of(findOrderPage, findOrderMenus))
                        .build(),
                HttpStatus.OK
        );
    }

    /*
        주문 단일 및 상세 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResOrderGetByIdDTO>> readById(Long orderId) {

        Order findOrder = getOrderById(orderId);

        List<OrderMenu> findOrderMenus = orderMenuRepository.findByOrderId(orderId);

        return new ResponseEntity<>(
                ResDto.<ResOrderGetByIdDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("주문 조회에 성공했습니다.")
                        .data(ResOrderGetByIdDTO.of(findOrder, findOrderMenus))
                        .build(),
                HttpStatus.OK
        );
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(User user, Long orderId) {

        Order order = user.getRole() == Role.CUSTOMER
                ? getOrderByIdAndUser(user, orderId)
                : getOrderById(orderId);

        order.cancelOrder();
    }

    // 주문 거절
    @Transactional
    public void rejectOrder(User user, Long orderId) {

        Order order = user.getRole() == Role.OWNER
                ? getOrderByIdAndOwner(user, orderId)
                : getOrderById(orderId);

        order.rejectOrder(user);
    }


    // 주문 삭제
    @Transactional
    public void deleteOrder(User user, Long orderId) {
        getOrderById(orderId).deleteOrder(user);
    }

    /*
        =========== private 메서드 ===========
    */

    /*
        주문 생성 private 메서드
    */

    private List<OrderMenu> getOrderMenusByUserAndStoreId(ReqOrderPostDTO dto) {
        List<UUID> menuIds = dto.getOrderItems().stream()
                .map(ReqOrderPostDTO.OrderItem::getMenuId)
                .toList();

        Map<UUID, Menu> menuMap = menuRepository.findByIdsAndIsDeletedFalse(menuIds).stream()
                .collect(Collectors.toMap(Menu::getMenuId, menu -> menu));

        return dto.getOrderItems().stream()
                .map(orderItem -> createOrderMenu(orderItem, menuMap))
                .collect(Collectors.toList());
    }

    private OrderMenu createOrderMenu(ReqOrderPostDTO.OrderItem orderItem, Map<UUID, Menu> menuMap) {
        // 메뉴 ID로 메뉴 정보 조회
        Menu menu = menuMap.get(orderItem.getMenuId());

        // OrderMenu 객체 생성
        return OrderMenu.builder()
                .menu(menu)
                .orderCount(orderItem.getCount())
                .orderPrice(menu.getMenuPrice() * orderItem.getCount()) // 메뉴 가격 * 수량
                .build();
    }

    /*
        주문 조회 private 메서드
    */

    private Order getOrderById(Long orderId) {
        return orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private Order getOrderByIdAndUser(User user, Long orderId) {
        return orderRepository.findByIdAndUser_UserId(orderId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("접근 권한이 없습니다."));
    }

    private Order getOrderByIdAndOwner(User user, Long orderId) {
        return orderRepository.findByIdAndStore_User_UserId(orderId, user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("접근 권한이 없습니다."));
    }


    private List<OrderMenu> getOrderMenusByOrderIds(List<Long> orderIds) {
        return orderMenuRepository.findByOrderIds(orderIds);
    }

    private static List<Long> getIdsByOrders(Page<Order> orders) {
        return orders.getContent().stream()
                .map(Order::getId)
                .toList();
    }

    /*
        유저 조회 private 메서드
    */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}
