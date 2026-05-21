package ru.persea.userservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;
import ru.persea.userservice.entity.UserAllergenEntity;
import ru.persea.userservice.mapper.UserActionMapper;
import ru.persea.userservice.mapper.UserAllergenMapper;
import ru.persea.userservice.repository.UserActionTypesRepository;
import ru.persea.userservice.repository.UserActionsRepository;
import ru.persea.userservice.repository.UserAllergensRepository;

@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserActionsRepository actionsRepo;
    private final UserActionTypesRepository actionTypesRepo;
    private final UserAllergensRepository allergensRepo;
    private final UserActionMapper actionMapper;
    private final UserAllergenMapper allergenMapper;

    @Override
    public List<ProductDto> getViewedProducts(UUID userId) {
        return getProductsByAction(userId, "view");
    }

    @Override
    public List<ProductDto> getFavoriteProducts(UUID userId) {
        return getProductsByAction(userId, "like");
    }

    @Override
    @Transactional
    public void addViewedProduct(UUID userId, Long productId) {
        var action = UserActionEvent.builder()
                        .userId(userId)
                        .productId(productId)
                        .type("view")
                        .build();
        saveAction(action);
    }

    @Override
    @Transactional
    public void addFavoriteProduct(UUID userId, Long productId) {
        var action = UserActionEvent.builder()
                        .userId(userId)
                        .productId(productId)
                        .type("like")
                        .build();
        saveAction(action);
    }

    @Override
    @Transactional
    public void deleteFavoriteProduct(UUID userId, Long productId) {
        actionsRepo.deleteByUserIdAndProductId(userId, productId, "like");
    }

    @Override
    public List<FactorDto> getAllergens(UUID userId) {
        return allergensRepo.findByKeycloakId(userId).stream()
                .map(allergenMapper::toFactorDto)
                .toList();
    }

    @Override
    @Transactional
    public void addAllergen(UUID userId, Long factorId) {
        UserAllergenEntity allergenEntity = new UserAllergenEntity(null, userId, factorId);
        allergensRepo.save(allergenEntity);
    }

    @Override
    @Transactional
    public void deleteAllergen(UUID userId, Long factorId) {
        allergensRepo.deleteByKeycloakIdAndFactorId(userId, factorId);
    }

    private List<ProductDto> getProductsByAction(UUID id, String action) {
        return actionsRepo.findByUserId(id, action).stream()
                .map(actionMapper::toViewedProductDto)
                .toList();
    }

    @Transactional
    public UserActionEntity saveAction(UserActionEvent action) {
        var actionTypeEntity = actionTypesRepo.findByName(action.type()).orElseThrow();
        var actionEntity = new UserActionEntity().builder()
                                .userId(action.userId())
                                .productId(action.productId())
                                .type(actionTypeEntity)
                                .createdAt(action.createdAt() == null ? Instant.now() : action.createdAt())
                                .build();
        return actionsRepo.save(actionEntity);
    }
}
