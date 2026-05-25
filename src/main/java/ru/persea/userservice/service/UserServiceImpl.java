package ru.persea.userservice.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;
import ru.persea.userservice.mapper.UserActionMapper;
import ru.persea.userservice.repository.JdbcUserActionRepository;
import ru.persea.userservice.repository.ProductsRepository;
import ru.persea.userservice.repository.UserActionTypesRepository;
import ru.persea.userservice.repository.UserActionsRepository;
import ru.persea.userservice.repository.projection.ProductViewProjection;

@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserActionsRepository actionsRepo;
    private final UserActionTypesRepository actionTypesRepo;
    private final ProductsRepository productsRepo;
    private final UserActionMapper actionMapper;
    
    private final JdbcUserActionRepository jdbcUserActionRepository;

    @Override
    public List<ProductDto> getViewedProducts(UUID userId) {
        return getProductByActionTypeAndUserId(userId, "view");
    }

    @Override
    public List<ProductDto> getScannedProducts(UUID userId) {
        return getProductByActionTypeAndUserId(userId, "scan");
    }

    @Override
    public List<ProductDto> getFavoriteProducts(UUID userId) {
        return getProductByActionTypeAndUserId(userId, "like");
    }

    @Override
    @Transactional
    public void addFavoriteProduct(UUID userId, Long productId) {
        saveAction(new UserActionEvent(userId, productId, "like", Instant.now()));
    }

    @Override
    @Transactional
    public void deleteFavoriteProduct(UUID userId, Long productId) {
        actionsRepo.deleteByUserIdAndProductId(userId, productId, "like");
    }

    @Override
    @Transactional
    public UserActionEntity saveAction(UserActionEvent action) {
        var actionType = actionTypesRepo.findByName(action.type())
            .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип действия: " + action.type()));
            
        var entity = UserActionEntity.builder()
            .userId(action.userId())
            .productId(action.productId())
            .type(actionType)
            .createdAt(action.createdAt() != null ? action.createdAt() : Instant.now())
            .build();
        return actionsRepo.save(entity);
    }

    @Override
    @Transactional
    public void syncAction(UserActionEvent action) {
        Long actionTypeId = actionTypesRepo.findByName(action.type())
            .map(UserActionTypeEntity::getId)
            .orElseThrow(() -> new IllegalArgumentException("Неизвестный тип действия: " + action.type()));
            
        Instant createdAt = action.createdAt() != null ? action.createdAt() : Instant.now();
        jdbcUserActionRepository.save(action.userId(), action.productId(), actionTypeId, createdAt);
    }

    private List<ProductDto> getProductByActionTypeAndUserId(UUID userId, String typeName) {
        return productsRepo.findByUserId(userId, typeName).stream().map(p -> {
            BrandDto brand = null;
            if (p.getBrandId() != null) brand = new BrandDto(p.getBrandId(), p.getBrandName());

            CategoryDto category = null;
            if (p.getCategoryId() != null) category = new CategoryDto(p.getCategoryId(), p.getCategoryName(), p.getCategoryCode());

            return new ProductDto(
                p.getId(),
                p.getName(),
                brand,
                category,
                p.getRating(),
                p.getImageUri()
            );
        }).toList();
    }
}