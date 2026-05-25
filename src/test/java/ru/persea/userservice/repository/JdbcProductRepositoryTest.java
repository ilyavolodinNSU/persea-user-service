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
class JdbcProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcProductRepository jdbcProductRepository;

    @Autowired
    private JdbcBrandRepository jdbcBrandRepository;

    @Autowired
    private JdbcCategoryRepository jdbcCategoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Продукт зависит от brand и category — создаём их перед каждым тестом
    void insertDependencies() {
        jdbcBrandRepository.upsert(1L, "Nike");
        jdbcCategoryRepository.upsert(1L, "Обувь", "shoes");
    }

    @Test
    void upsert_insertsNewProduct() {
        insertDependencies();
        jdbcProductRepository.upsert(1L, "Air Max", 1L, 1L, 90, "http://img.png");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE id = 1 AND name = 'Air Max'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void upsert_updatesExistingProduct() {
        insertDependencies();
        jdbcProductRepository.upsert(2L, "Air Max", 1L, 1L, 90, "http://img.png");
        jdbcProductRepository.upsert(2L, "Air Max Updated", 1L, 1L, 95, "http://img2.png");

        String name = jdbcTemplate.queryForObject(
                "SELECT name FROM products WHERE id = 2",
                String.class
        );
        assertThat(name).isEqualTo("Air Max Updated");
    }

    @Test
    void deleteById_removesProduct() {
        insertDependencies();
        jdbcProductRepository.upsert(3L, "Air Force", 1L, 1L, 85, "http://img3.png");
        jdbcProductRepository.deleteById(3L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE id = 3",
                Integer.class
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteById_whenProductDoesNotExist_doesNothing() {
        jdbcProductRepository.deleteById(999L);
    }
}