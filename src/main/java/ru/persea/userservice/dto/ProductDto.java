package ru.persea.userservice.dto;

public record ProductDto(
    Long id,
    String name, 
    BrandDto brand,
    CategoryDto category,
    Integer rating,
    String imageURI
) {}
