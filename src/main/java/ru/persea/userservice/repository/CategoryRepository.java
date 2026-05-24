package ru.persea.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.entity.CategoryEntity;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
 
}
