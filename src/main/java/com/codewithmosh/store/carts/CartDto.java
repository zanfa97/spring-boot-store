package com.codewithmosh.store.carts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CartDto {

    private UUID id;
    
    @Schema(example = "[]")
    private List<CartItemDto> cartItems = new ArrayList<>();
    
    @Schema(example = "0.00")
    private BigDecimal totalPrice = BigDecimal.ZERO;

}
