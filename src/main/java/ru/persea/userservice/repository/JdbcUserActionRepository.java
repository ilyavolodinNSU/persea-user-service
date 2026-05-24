package ru.persea.userservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcUserActionRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(UUID userId, Long productId, Long actionTypeId, Instant createdAt) {
        String sql = """
            INSERT INTO user_actions (keycloak_id, product_id, type_id, created_at)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (keycloak_id, product_id, type_id) DO UPDATE SET
                created_at = EXCLUDED.created_at
            """;
        jdbcTemplate.update(sql, userId, productId, actionTypeId, Timestamp.from(createdAt));
    }

    public void delete(UUID userId, Long productId, Long actionTypeId) {
        jdbcTemplate.update(
            "DELETE FROM user_actions WHERE keycloak_id = ? AND product_id = ? AND type_id = ?",
            userId, productId, actionTypeId
        );
    }
}