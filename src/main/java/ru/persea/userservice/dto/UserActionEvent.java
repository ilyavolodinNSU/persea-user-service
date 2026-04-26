package ru.persea.userservice.dto;

import java.util.UUID;

public record UserActionEvent(
    UUID userId,
    String action
) {}
