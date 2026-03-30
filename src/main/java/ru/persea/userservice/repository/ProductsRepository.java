package ru.persea.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.persea.userservice.entity.ProductEntity;

@Repository
public interface ProductsRepository extends JpaRepository<ProductEntity, Long> {

}
