package ru.persea.userservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.dto.FactorDto;
import ru.persea.userservice.entity.UserAllergenEntity;

@Repository
public interface UserAllergensRepository extends JpaRepository<UserAllergenEntity, Long> {
    public List<UserAllergenEntity> findByKeycloakId(UUID uuid);

    public void deleteByKeycloakIdAndFactorId(UUID userId, Long factorId);
}
