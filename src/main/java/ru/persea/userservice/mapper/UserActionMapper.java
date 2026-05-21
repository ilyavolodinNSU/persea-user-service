package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.UserActionEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserActionMapper {
    public ProductDto toViewedProductDto(UserActionEntity entity);
    
}
