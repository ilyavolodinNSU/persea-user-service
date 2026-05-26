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
class JdbcBrandRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcBrandRepository jdbcBrandRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void upsert_insertsNewBrand() {
        jdbcBrandRepository.upsert(1L, "Nike");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM brands WHERE id = 1 AND name = 'Nike'",
                Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void upsert_updatesExistingBrand() {
        jdbcBrandRepository.upsert(2L, "Adidas");
        jdbcBrandRepository.upsert(2L, "Adidas Updated");

        String name = jdbcTemplate.queryForObject(
                "SELECT name FROM brands WHERE id = 2",
                String.class
        );
        assertThat(name).isEqualTo("Adidas Updated");
    }

    @Test
    void deleteById_removesBrand() {
        jdbcBrandRepository.upsert(3L, "Puma");
        jdbcBrandRepository.deleteById(3L);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM brands WHERE id = 3",
                Integer.class
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    void deleteById_whenBrandDoesNotExist_doesNothing() {
        jdbcBrandRepository.deleteById(999L);
    }
}