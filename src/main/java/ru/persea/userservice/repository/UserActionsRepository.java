package ru.persea.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.UserActionEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserActionsRepository extends JpaRepository<UserActionEntity, Long> {

    @Modifying
    @Query("""
        DELETE FROM UserActionEntity ua
        WHERE ua.userId = :userId
          AND ua.productId = :productId
          AND ua.type.name = :typeName
    """)
    void deleteByUserIdAndProductId(
        @Param("userId") UUID userId,
        @Param("productId") Long productId,
        @Param("typeName") String typeName
    );
}
