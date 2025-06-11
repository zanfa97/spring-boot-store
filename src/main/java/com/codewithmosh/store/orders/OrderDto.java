package com.codewithmosh.store.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.codewithmosh.store.payments.PaymentStatus;

import lombok.Data;

@Data
public class OrderDto {

    private Long id;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
    private BigDecimal totalPrice;

}
