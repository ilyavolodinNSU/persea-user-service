package ru.persea.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductSyncDto (
    Long id,
    String name,
    @JsonProperty("brand_id")
    Long brandId,
    @JsonProperty("category_id")
    Long categoryId,
    Integer rating,
    @JsonProperty("image_uri")
    String imageUri,
    @JsonProperty("updated_at")
    Long updatedAt
) {}
