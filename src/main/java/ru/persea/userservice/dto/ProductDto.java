package ru.persea.userservice.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductDto(
    Long id,
    String name,
    Integer rating,
    @JsonProperty("image_uri") String imageURI,
    Instant createdAt
) {}
