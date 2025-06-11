package com.codewithmosh.store.orders;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    OrderDto toDto(Order order);

}
