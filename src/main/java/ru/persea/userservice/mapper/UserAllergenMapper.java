package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;

import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.entity.UserAllergenEntity;

@Mapper(componentModel = "spring")
public interface UserAllergenMapper {
    public FactorDto toFactorDto(UserAllergenEntity entity);
}
