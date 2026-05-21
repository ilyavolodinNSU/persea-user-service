package ru.persea.userservice.dto;

public record ProductCdcMessage (
    ProductSyncDto before,
    ProductSyncDto after,
    String op
) {}
