package ru.persea.userservice.service;

import java.util.List;
import java.util.UUID;

import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.entity.UserActionEntity;

public interface UserService {
    public List<ProductDto> getViewedProducts(UUID userId);

    public List<ProductDto> getFavoriteProducts(UUID userId); 

    public void addFavoriteProduct(UUID userId, Long productId);

    public void deleteFavoriteProduct(UUID userId, Long productId);

    public UserActionEntity saveAction(UserActionEvent action);

    public void syncAction(UserActionEvent action);
}
