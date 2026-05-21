package ru.persea.userservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.entity.UserActionEntity;

@Repository
public interface UserActionsRepository extends JpaRepository<UserActionEntity, Long> {
    @Query("""
        select ua 
        from UserActionEntity ua 
        join ua.type t 
        where ua.userId = :userId and t.name = :typeName
        order by ua.createdAt desc
    """)
    public List<UserActionEntity> findByUserId(
        @Param("userId") UUID userId,
        @Param("typeName") String typeName
    );

    @Query("""
        delete
        from UserActionEntity ua 
        where ua.userId = :userId 
            and ua.productId = :productId 
            and ua.type.name = :typeName
    """)
    public List<UserActionEntity> deleteByUserIdAndProductId(
        @Param("userId") UUID userId,
        @Param("productId") Long productId,
        @Param("typeName") String typeName
    );
}
