package ru.persea.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;
import ru.persea.userservice.mapper.UserActionMapper;
import ru.persea.userservice.repository.JdbcUserActionRepository;
import ru.persea.userservice.repository.ProductsRepository;
import ru.persea.userservice.repository.UserActionTypesRepository;
import ru.persea.userservice.repository.UserActionsRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserActionsRepository actionsRepo;
    @Mock
    private UserActionTypesRepository actionTypesRepo;
    @Mock
    private ProductsRepository productsRepo;
    @Mock
    private UserActionMapper actionMapper;
    @Mock
    private JdbcUserActionRepository jdbcUserActionRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // --- saveAction ---

    @Test
    void saveAction_whenValidType_savesAndReturnsEntity() {
        // Готовим данные
        UUID userId = UUID.randomUUID();
        UserActionEvent event = new UserActionEvent(userId, 1L, "view", Instant.now());

        UserActionTypeEntity actionType = new UserActionTypeEntity();
        UserActionEntity savedEntity = new UserActionEntity();

        // Говорим моку: когда спросят тип "view" — верни его
        when(actionTypesRepo.findByName("view")).thenReturn(Optional.of(actionType));
        // Говорим моку: когда попросят сохранить — верни entity
        when(actionsRepo.save(any())).thenReturn(savedEntity);

        // Вызываем метод
        UserActionEntity result = userService.saveAction(event);

        // Проверяем результат
        assertThat(result).isEqualTo(savedEntity);
        verify(actionsRepo).save(any(UserActionEntity.class));
    }

    @Test
    void saveAction_whenUnknownType_throwsException() {
        UUID userId = UUID.randomUUID();
        UserActionEvent event = new UserActionEvent(userId, 1L, "unknown_type", Instant.now());

        // Мок возвращает пустой Optional — тип не найден
        when(actionTypesRepo.findByName("unknown_type")).thenReturn(Optional.empty());

        // Проверяем что метод бросает именно IllegalArgumentException
        assertThatThrownBy(() -> userService.saveAction(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неизвестный тип действия");
    }

    @Test
    void saveAction_whenCreatedAtIsNull_usesCurrentTime() {
        UUID userId = UUID.randomUUID();
        // createdAt = null — сервис должен подставить Instant.now() сам
        UserActionEvent event = new UserActionEvent(userId, 1L, "like", null);

        UserActionTypeEntity actionType = new UserActionTypeEntity();
        UserActionEntity savedEntity = new UserActionEntity();

        when(actionTypesRepo.findByName("like")).thenReturn(Optional.of(actionType));
        when(actionsRepo.save(any())).thenReturn(savedEntity);

        UserActionEntity result = userService.saveAction(event);

        assertThat(result).isNotNull();
        verify(actionsRepo).save(any(UserActionEntity.class));
    }

    // --- addFavoriteProduct ---

    @Test
    void addFavoriteProduct_callsSaveActionWithLikeType() {
        UUID userId = UUID.randomUUID();
        Long productId = 42L;

        UserActionTypeEntity actionType = new UserActionTypeEntity();
        when(actionTypesRepo.findByName("like")).thenReturn(Optional.of(actionType));
        when(actionsRepo.save(any())).thenReturn(new UserActionEntity());

        userService.addFavoriteProduct(userId, productId);

        // Проверяем что репозиторий был вызван с типом "like"
        verify(actionTypesRepo).findByName("like");
        verify(actionsRepo).save(any(UserActionEntity.class));
    }

    // --- deleteFavoriteProduct ---

    @Test
    void deleteFavoriteProduct_callsRepository() {
        UUID userId = UUID.randomUUID();
        Long productId = 99L;

        userService.deleteFavoriteProduct(userId, productId);

        verify(actionsRepo).deleteByUserIdAndProductId(userId, productId, "like");
    }

    // --- getViewedProducts ---

    @Test
    void getViewedProducts_whenNoActions_returnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(productsRepo.findByUserId(userId, "view")).thenReturn(List.of());

        var result = userService.getViewedProducts(userId);

        assertThat(result).isEmpty();
    }

    // --- getFavoriteProducts ---

    @Test
    void getFavoriteProducts_whenNoActions_returnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(productsRepo.findByUserId(userId, "like")).thenReturn(List.of());

        var result = userService.getFavoriteProducts(userId);

        assertThat(result).isEmpty();
    }
}
