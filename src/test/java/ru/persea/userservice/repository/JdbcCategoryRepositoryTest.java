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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class JdbcCategoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcCategoryRepository jdbcCategoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void upsert_insertsNewCategory() {
        jdbcCategoryRepository.upsert(1L, "Электроника", "electronics");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE id = 1 AND name = 'Электроника' AND code = 'electronics'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void upsert_updatesExistingCategory() {
        jdbcCategoryRepository.upsert(2L, "Одежда", "clothes");
        jdbcCategoryRepository.upsert(2L, "Одежда Updated", "clothes-updated");

        String name = jdbcTemplate.queryForObject(
                "SELECT name FROM categories WHERE id = 2",
                String.class
        );
        assertThat(name).isEqualTo("Одежда Updated");
    }

    @Test
    void deleteById_removesCategory() {
        jdbcCategoryRepository.upsert(3L, "Книги", "books");
        jdbcCategoryRepository.deleteById(3L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categories WHERE id = 3",
                Integer.class
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteById_whenCategoryDoesNotExist_doesNothing() {
        jdbcCategoryRepository.deleteById(999L);
    }
}