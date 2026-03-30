package ru.persea.userservice.dto;

import java.time.Instant;

public record ProductDto(
    Long id,
    String name,
    Integer raiting,
    String imageURI,
    Instant createdAt
) {}
