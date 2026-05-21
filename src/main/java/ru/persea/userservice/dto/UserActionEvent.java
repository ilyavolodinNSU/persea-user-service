package ru.persea.userservice.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UserActionEvent(
    UUID userId,
    Long productId,
    String type,
    Instant createdAt
) {}
