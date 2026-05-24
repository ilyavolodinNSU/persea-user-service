package ru.persea.userservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcProductRepository {
    private final JdbcTemplate jdbcTemplate;

    public void upsert(Long id, String name, Long brandId, Long categoryId, 
                       Integer rating, String imageURI) {
        String sql = """
            INSERT INTO products (id, name, brand_id, category_id, rating, image_uri)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                brand_id = EXCLUDED.brand_id,
                category_id = EXCLUDED.category_id,
                rating = EXCLUDED.rating,
                image_uri = EXCLUDED.image_uri
            """;
        jdbcTemplate.update(sql, id, name, brandId, categoryId, rating, imageURI);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }
}