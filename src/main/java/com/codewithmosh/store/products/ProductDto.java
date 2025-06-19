package com.codewithmosh.store.products;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Data
@Schema(name = "StoreProduct", description = "Product information in the store catalog")
public class ProductDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Byte categoryId;
}
