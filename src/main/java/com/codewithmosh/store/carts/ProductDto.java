package com.codewithmosh.store.carts;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private BigDecimal price;

}
