package ru.persea.userservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserActionsRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserActionsRepository userActionsRepository;

    @Autowired
    private UserActionTypesRepository userActionTypesRepository;

    @Autowired
    private JdbcBrandRepository jdbcBrandRepository;

    @Autowired
    private JdbcCategoryRepository jdbcCategoryRepository;

    @Autowired
    private JdbcProductRepository jdbcProductRepository;

    void insertDependencies() {
        jdbcBrandRepository.upsert(1L, "Nike");
        jdbcCategoryRepository.upsert(1L, "Обувь", "shoes");
        jdbcProductRepository.upsert(1L, "Air Max", 1L, 1L, 90, "http://img.png");
    }

    @Test
    @Transactional
    void deleteByUserIdAndProductId_removesCorrectAction() {
        insertDependencies();
        UUID userId = UUID.randomUUID();

        UserActionTypeEntity likeType = userActionTypesRepository.findByName("like").orElseThrow();

        UserActionEntity action = UserActionEntity.builder()
                .userId(userId)
                .productId(1L)
                .type(likeType)
                .createdAt(Instant.now())
                .build();
        userActionsRepository.save(action);

        assertThat(userActionsRepository.findAll())
                .anyMatch(a -> a.getUserId().equals(userId));

        userActionsRepository.deleteByUserIdAndProductId(userId, 1L, "like");

        assertThat(userActionsRepository.findAll())
                .noneMatch(a -> a.getUserId().equals(userId));
    }

    @Test
    @Transactional
    void deleteByUserIdAndProductId_doesNotDeleteOtherActions() {
        insertDependencies();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        UserActionTypeEntity viewType = userActionTypesRepository.findByName("view").orElseThrow();

        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId1).productId(1L).type(viewType).createdAt(Instant.now()).build());
        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId2).productId(1L).type(viewType).createdAt(Instant.now()).build());

        userActionsRepository.deleteByUserIdAndProductId(userId1, 1L, "view");

        // userId2 должен остаться
        assertThat(userActionsRepository.findAll())
                .anyMatch(a -> a.getUserId().equals(userId2));
    }
}