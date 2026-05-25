package ru.persea.userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.repository.JdbcBrandRepository;
import ru.persea.userservice.repository.JdbcCategoryRepository;
import ru.persea.userservice.repository.JdbcProductRepository;
import ru.persea.userservice.repository.UserActionsRepository;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final JdbcProductRepository jdbcProductRepository;
    private final JdbcBrandRepository jdbcBrandRepository;
    private final JdbcCategoryRepository jdbcCategoryRepository;
    private final UserActionsRepository userActionsRepository;

    @Override
    @Transactional
    public void syncProduct(ProductSyncDto product) {
        jdbcProductRepository.upsert(
            product.id(),
            product.name(),
            product.brandId(),
            product.categoryId(),
            product.rating(),
            product.imageURI()
        );
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        jdbcProductRepository.deleteById(productId);
    }

    @Override
    @Transactional
    public void syncBrand(BrandDto brand) {
        jdbcBrandRepository.upsert(brand.id(), brand.name());
    }

    @Override
    @Transactional
    public void deleteBrand(Long brandId) {
        jdbcBrandRepository.deleteById(brandId);
    }

    @Override
    @Transactional
    public void syncCategory(CategoryDto category) {
        jdbcCategoryRepository.upsert(category.id(), category.name(), category.code());
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        jdbcCategoryRepository.deleteById(categoryId);
    }
}