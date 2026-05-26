package ru.persea.userservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.repository.JdbcBrandRepository;
import ru.persea.userservice.repository.JdbcCategoryRepository;
import ru.persea.userservice.repository.JdbcProductRepository;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private JdbcProductRepository jdbcProductRepository;
    @Mock private JdbcBrandRepository jdbcBrandRepository;
    @Mock private JdbcCategoryRepository jdbcCategoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // --- syncProduct ---

    @Test
    void syncProduct_callsRepositoryWithCorrectParams() {
        ProductSyncDto product = new ProductSyncDto(1L, "Телефон", 10L, 20L, 4, "http://img.png");

        productService.syncProduct(product);

        verify(jdbcProductRepository).upsert(1L, "Телефон", 10L, 20L, 4, "http://img.png");
    }

    // --- deleteProduct ---

    @Test
    void deleteProduct_callsRepositoryWithCorrectId() {
        productService.deleteProduct(5L);

        verify(jdbcProductRepository).deleteById(5L);
    }

    // --- syncBrand ---

    @Test
    void syncBrand_callsRepositoryWithCorrectParams() {
        BrandDto brand = new BrandDto(3L, "Nike");

        productService.syncBrand(brand);

        verify(jdbcBrandRepository).upsert(3L, "Nike");
    }

    // --- deleteBrand ---

    @Test
    void deleteBrand_callsRepositoryWithCorrectId() {
        productService.deleteBrand(3L);

        verify(jdbcBrandRepository).deleteById(3L);
    }

    // --- syncCategory ---

    @Test
    void syncCategory_callsRepositoryWithCorrectParams() {
        CategoryDto category = new CategoryDto(7L, "Электроника", "electronics");

        productService.syncCategory(category);

        verify(jdbcCategoryRepository).upsert(7L, "Электроника", "electronics");
    }

    // --- deleteCategory ---

    @Test
    void deleteCategory_callsRepositoryWithCorrectId() {
        productService.deleteCategory(7L);

        verify(jdbcCategoryRepository).deleteById(7L);
    }
}