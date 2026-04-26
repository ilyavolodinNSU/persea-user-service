package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.ProductEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    public ProductEntity toEntity(ProductDto dto);

    public ProductDto toDto(ProductEntity enity);
}
