package ru.persea.userservice.dto;

import java.time.Instant;

public record ProductDto(
    Long id,
    String name,
    Integer rating,
    String imageURI,
    Instant createdAt
) {}
