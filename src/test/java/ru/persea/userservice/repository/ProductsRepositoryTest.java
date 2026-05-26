package ru.persea.userservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;
import ru.persea.userservice.repository.projection.ProductViewProjection;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ProductsRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductsRepository productsRepository;

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
        jdbcProductRepository.upsert(2L, "Air Force", 1L, 1L, 85, "http://img2.png");
    }

    @Test
    void findByUserId_returnsViewedProducts() {
        insertDependencies();
        UUID userId = UUID.randomUUID();

        UserActionTypeEntity viewType = userActionTypesRepository.findByName("view").orElseThrow();
        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId).productId(1L).type(viewType).createdAt(Instant.now()).build());

        List<ProductViewProjection> result = productsRepository.findByUserId(userId, "view");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Air Max");
        assertThat(result.get(0).getBrandName()).isEqualTo("Nike");
        assertThat(result.get(0).getCategoryCode()).isEqualTo("shoes");
    }

    @Test
    void findByUserId_returnsOnlyCorrectType() {
        insertDependencies();
        UUID userId = UUID.randomUUID();

        UserActionTypeEntity viewType = userActionTypesRepository.findByName("view").orElseThrow();
        UserActionTypeEntity likeType = userActionTypesRepository.findByName("like").orElseThrow();

        // Сохраняем view для продукта 1 и like для продукта 2
        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId).productId(1L).type(viewType).createdAt(Instant.now()).build());
        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId).productId(2L).type(likeType).createdAt(Instant.now()).build());

        List<ProductViewProjection> views = productsRepository.findByUserId(userId, "view");
        List<ProductViewProjection> likes = productsRepository.findByUserId(userId, "like");

        assertThat(views).hasSize(1);
        assertThat(views.get(0).getName()).isEqualTo("Air Max");

        assertThat(likes).hasSize(1);
        assertThat(likes.get(0).getName()).isEqualTo("Air Force");
    }

    @Test
    void findByUserId_whenNoActions_returnsEmptyList() {
        UUID userId = UUID.randomUUID();

        List<ProductViewProjection> result = productsRepository.findByUserId(userId, "view");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUserId_doesNotReturnOtherUsersProducts() {
        insertDependencies();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        UserActionTypeEntity viewType = userActionTypesRepository.findByName("view").orElseThrow();
        userActionsRepository.save(UserActionEntity.builder()
                .userId(userId1).productId(1L).type(viewType).createdAt(Instant.now()).build());

        List<ProductViewProjection> result = productsRepository.findByUserId(userId2, "view");

        assertThat(result).isEmpty();
    }
}