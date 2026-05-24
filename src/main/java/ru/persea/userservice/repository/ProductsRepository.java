package ru.persea.userservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.ProductEntity;
import ru.persea.userservice.repository.projection.ProductViewProjection;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = """
        SELECT 
            p.id as id,
            p.name as name,
            b.id as brandId,
            b.name as brandName,
            c.id as categoryId,
            c.name as categoryName,
            c.code as categoryCode,
            p.rating as rating,
            p.image_uri as imageUri
        FROM user_actions ua
        JOIN products p ON p.id = ua.product_id
        LEFT JOIN brands b ON b.id = p.brand_id
        LEFT JOIN categories c ON c.id = p.category_id
        JOIN user_action_types t ON t.id = ua.type_id
        WHERE ua.keycloak_id = :userId 
          AND t.name = :typeName
        ORDER BY ua.created_at DESC
        """, nativeQuery = true)
    List<ProductViewProjection> findByUserId(
        @Param("userId") UUID userId, 
        @Param("typeName") String typeName
    );
}
