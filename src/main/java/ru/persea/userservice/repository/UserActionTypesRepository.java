package ru.persea.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.entity.UserActionTypeEntity;

@Repository
public interface UserActionTypesRepository extends JpaRepository<UserActionTypeEntity, Long> {
    public Optional<UserActionTypeEntity> findByName(String name);
}
