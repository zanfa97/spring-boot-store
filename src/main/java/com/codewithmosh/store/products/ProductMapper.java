package com.codewithmosh.store.products;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDto productDto);
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "id", ignore = true)
    void update(ProductDto productDto, @MappingTarget Product product);

}
