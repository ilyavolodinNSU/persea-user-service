package ru.persea.userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.mapper.ProductMapper;
import ru.persea.userservice.repository.ProductsRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductsRepository productsRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public void syncProduct(ProductSyncDto product) {
        productsRepository.save(productMapper.toEntity(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productsRepository.deleteById(productId);
    }
}
