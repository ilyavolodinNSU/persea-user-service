package ru.persea.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.entity.UserAllergenEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAllergenMapper {
    public FactorDto toFactorDto(UserAllergenEntity entity);
}
