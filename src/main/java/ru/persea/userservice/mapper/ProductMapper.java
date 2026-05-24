package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.entity.BrandEntity;
import ru.persea.userservice.entity.CategoryEntity;
import ru.persea.userservice.entity.ProductEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    public CategoryDto toDto(CategoryEntity entity);

    public BrandDto toDto(BrandEntity entity);

    public CategoryEntity toEntity(CategoryDto entity);

    public BrandEntity toEntity(BrandDto entity);

    public ProductEntity toEntity(ProductDto dto);

    public ProductDto toDto(ProductEntity enity);

    public ProductEntity toEntity(ProductSyncDto dto); 
}
