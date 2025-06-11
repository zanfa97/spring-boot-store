package com.codewithmosh.store.orders;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.auth.AuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var customer = authService.getCurrentUser();
        var orders = orderRepository.getOrdersByCustomer(customer);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository
                .getOrderWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);

        
        var user = authService.getCurrentUser();

        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You do not have access to this order");
        }

        return orderMapper.toDto(order);
    }

}
