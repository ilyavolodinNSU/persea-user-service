package ru.persea.userservice.dto;

public record CategoryDto(
    Long id,
    String name,
    String code
) {}