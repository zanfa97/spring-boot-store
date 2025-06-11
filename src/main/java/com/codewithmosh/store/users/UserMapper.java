package com.codewithmosh.store.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    User toEntity(RegisterUserRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "password", ignore = true)
    void update(UpdateUserRequest request, @MappingTarget User user);
}
