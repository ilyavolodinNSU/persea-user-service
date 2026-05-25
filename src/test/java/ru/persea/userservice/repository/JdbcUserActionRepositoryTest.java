package ru.persea.userservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class JdbcUserActionRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcUserActionRepository jdbcUserActionRepository;

    @Autowired
    private JdbcBrandRepository jdbcBrandRepository;

    @Autowired
    private JdbcCategoryRepository jdbcCategoryRepository;

    @Autowired
    private JdbcProductRepository jdbcProductRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // user_actions зависит от products и user_action_types
    // user_action_types уже заполнены миграцией (like=1, view=2, scan=3)
    void insertDependencies() {
        jdbcBrandRepository.upsert(1L, "Nike");
        jdbcCategoryRepository.upsert(1L, "Обувь", "shoes");
        jdbcProductRepository.upsert(1L, "Air Max", 1L, 1L, 90, "http://img.png");
    }

    @Test
    void save_insertsNewUserAction() {
        insertDependencies();
        UUID userId = UUID.randomUUID();

        jdbcUserActionRepository.save(userId, 1L, 2L, Instant.now()); // type_id=2 это "view"

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_actions WHERE keycloak_id = ? AND product_id = 1 AND type_id = 2",
                Integer.class, userId
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void save_onConflict_updatesCreatedAt() {
        insertDependencies();
        UUID userId = UUID.randomUUID();
        Instant first = Instant.parse("2024-01-01T00:00:00Z");
        Instant second = Instant.parse("2024-06-01T00:00:00Z");

        jdbcUserActionRepository.save(userId, 1L, 1L, first);   // type_id=1 это "like"
        jdbcUserActionRepository.save(userId, 1L, 1L, second);  // тот же userId+productId+typeId

        Instant savedAt = jdbcTemplate.queryForObject(
                "SELECT created_at FROM user_actions WHERE keycloak_id = ? AND product_id = 1 AND type_id = 1",
                (rs, rn) -> rs.getTimestamp("created_at").toInstant(),
                userId
        );
        assertThat(savedAt).isEqualTo(second);
    }

    @Test
    void delete_removesUserAction() {
        insertDependencies();
        UUID userId = UUID.randomUUID();

        jdbcUserActionRepository.save(userId, 1L, 3L, Instant.now()); // type_id=3 это "scan"
        jdbcUserActionRepository.delete(userId, 1L, 3L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_actions WHERE keycloak_id = ? AND product_id = 1 AND type_id = 3",
                Integer.class, userId
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    void delete_whenActionDoesNotExist_doesNothing() {
        jdbcUserActionRepository.delete(UUID.randomUUID(), 999L, 1L);
    }
}