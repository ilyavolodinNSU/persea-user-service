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
import ru.persea.userservice.entity.UserActionTypeEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserActionTypesRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserActionTypesRepository userActionTypesRepository;

    @Test
    void findByName_whenExists_returnsType() {
        // like, view, scan уже вставлены миграцией
        Optional<UserActionTypeEntity> result = userActionTypesRepository.findByName("like");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("like");
    }

    @Test
    void findByName_whenNotExists_returnsEmpty() {
        Optional<UserActionTypeEntity> result = userActionTypesRepository.findByName("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByName_allThreeTypesExist() {
        assertThat(userActionTypesRepository.findByName("like")).isPresent();
        assertThat(userActionTypesRepository.findByName("view")).isPresent();
        assertThat(userActionTypesRepository.findByName("scan")).isPresent();
    }
}