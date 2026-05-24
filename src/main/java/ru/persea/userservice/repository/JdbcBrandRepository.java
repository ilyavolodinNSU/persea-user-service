package ru.persea.userservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcBrandRepository {
    private final JdbcTemplate jdbcTemplate;

    public void upsert(Long id, String name) {
        String sql = """
            INSERT INTO brands (id, name) VALUES (?, ?)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name
            """;
        jdbcTemplate.update(sql, id, name);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM brands WHERE id = ?", id);
    }
}