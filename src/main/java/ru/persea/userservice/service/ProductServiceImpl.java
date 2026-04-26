package ru.persea.userservice.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.mapper.ProductMapper;
import ru.persea.userservice.repository.ProductsRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductsRepository productsRepository;
    private final ProductMapper productMapper;

    @Override
    public void addProduct(ProductDto product) {
        productsRepository.save(productMapper.toEntity(product));
    }

    @Override
    public void deleteProduct(Long productId) {
        productsRepository.deleteById(productId);
    }
}
