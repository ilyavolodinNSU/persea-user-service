package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.UserActionEntity;

@Mapper(componentModel = "spring")
public interface UserActionMapper {
    public ProductDto toViewedProductDto(UserActionEntity entity);
}
