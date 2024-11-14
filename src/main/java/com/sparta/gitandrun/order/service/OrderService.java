package com.sparta.gitandrun.order.service;


import com.sparta.gitandrun.menu.entity.Menu;
import com.sparta.gitandrun.menu.repository.MenuRepository;
import com.sparta.gitandrun.order.dto.req.CreateOrderReqDto;
import com.sparta.gitandrun.order.dto.res.ResDto;
import com.sparta.gitandrun.order.dto.res.ResOrderGetByIdDTO;
import com.sparta.gitandrun.order.dto.res.ResOrderGetDTO;
import com.sparta.gitandrun.order.entity.Order;
import com.sparta.gitandrun.order.entity.OrderMenu;
import com.sparta.gitandrun.order.repository.OrderMenuRepository;
import com.sparta.gitandrun.order.repository.OrderRepository;
import com.sparta.gitandrun.store.entity.Store;
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
    public void createOrder(CreateOrderReqDto dto) {

        /*
            유저 조회
            User findUser = userRepository.findUser(user.getId);
        */

        /*
            메뉴 조회 및 중복된 메뉴 개수 계산
        */

        User findUser = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        List<Menu> findMenus = getMenus(dto);

        Map<Menu, Long> menuCountMap = getMenuCountMap(dto, findMenus);

        /*
            주문 상품 생성
        */
        List<OrderMenu> orderMenus = OrderMenu.createOrderMenus(findMenus, menuCountMap);

        /*
            주문 생성
        */
        Order order = Order.createOrder(findUser, dto.isType(), orderMenus);

        /*
            주문 저장
        */
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResOrderGetDTO>> getBy(Long userId, Pageable pageable) {
        /*
            주문 조회 : userId 를 기준으로
        */
        Page<Order> findOrderPage = orderRepository.findByUser_UserIdAndIsDeletedFalse(userId, pageable);

        /*
            주문 목록 조회 : 앞서 구한 order 의 id 를 기준으로
        */
        List<OrderMenu> orderMenus = getOrderMenusBy(getIdsBy(findOrderPage));

        return new ResponseEntity<>(
                ResDto.<ResOrderGetDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("주문 조회에 성공했습니다.")
                        .data(ResOrderGetDTO.of(findOrderPage, orderMenus))
                        .build(),
                HttpStatus.OK
        );
    }

    /*
        주문 단일 및 상세 조회
    */
    @Transactional(readOnly = true)
    public ResponseEntity<ResDto<ResOrderGetByIdDTO>> getBy(Long orderId) {

        Order findOrder = getFindOrder(orderId);

        List<OrderMenu> findOrderMenus = orderMenuRepository.findByOrderId(orderId);

        Store store = findOrderMenus.get(0).getMenu().getStore();

        return new ResponseEntity<>(
                ResDto.<ResOrderGetByIdDTO>builder()
                        .code(HttpStatus.OK.value())
                        .message("주문 조회에 성공했습니다.")
                        .data(ResOrderGetByIdDTO.of(findOrder, findOrderMenus, store))
                        .build(),
                HttpStatus.OK
        );
    }

    private Order getFindOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private List<OrderMenu> getOrderMenusBy(List<Long> orderIds) {
        return orderMenuRepository.findByOrderIds(orderIds);
    }

    private static List<Long> getIdsBy(Page<Order> orders) {
        return orders.getContent().stream()
                .map(Order::getId)
                .toList();
    }


    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        /*
            본인 검증 메서드 추후 구현 예정
        */
//        private void checkOrderAccessPermission (User user, Order findOrder){
//            if (!user.getId().equals(findOrder.getUser().getId())) {
//                Objects.equals();
//                throw new IllegalArgumentException("해당 주문에 대한 접근 권한이 없습니다.");
//            }
//        }
//
        Order findOrder = getOrder(orderId);

        findOrder.cancelOrder();
    }

    // 주문 거절
    @Transactional
    public void rejectOrder(Long orderId) {

        Order findOrder = getOrder(orderId);

        /*
            본인 가게 주문 검증 메서드 구현 필요
        */

        findOrder.rejectOrder();
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다."));
    }

    private List<Menu> getMenus(CreateOrderReqDto dto) {
        List<Menu> findMenus = menuRepository.findByIdsAndIsDeletedFalse(dto.getMenuIds());

        if (findMenus.isEmpty()) {
            throw new NullPointerException("메뉴 목록이 비어있습니다.");
        }
        return findMenus;
    }

    private static Map<Menu, Long> getMenuCountMap(CreateOrderReqDto dto, List<Menu> findMenus) {
        /*
            1. 클라이언트로부터 전달 받은 menuId 의 리스트를 { 메뉴 Id : 개수 } 형태로 구성된 Map 으로 생성한다.
            2. 이때 Collectors.counting() 키 값의 개수를 세는 역할을 한다.
            3. 이후 { 메뉴 : 메뉴의 개수 } 형태로 구성된 Map 을 생성하여 반환한다.
        */
        Map<UUID, Long> uuidLongMap = dto.getMenuIds().stream()
                .collect(Collectors.groupingBy(menuId -> menuId, Collectors.counting()));

        return findMenus.stream()
                .collect(Collectors.toMap(
                        menu -> menu,
                        menu -> uuidLongMap.get(menu.getMenuId())
                ));
    }

}
