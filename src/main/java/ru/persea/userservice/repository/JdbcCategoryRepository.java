package ru.persea.userservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcCategoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public void upsert(Long id, String name, String code) {
        String sql = """
            INSERT INTO categories (id, name, code) VALUES (?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, code = EXCLUDED.code
            """;
        jdbcTemplate.update(sql, id, name, code);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
    }
}