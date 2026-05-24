package ru.persea.userservice.service;

import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductSyncDto;

public interface ProductService {
    public void syncProduct(ProductSyncDto product);

    public void deleteProduct(Long productId); 

    public void syncBrand(BrandDto brand);

    public void deleteBrand(Long brandId);

    public void syncCategory(CategoryDto category);

    public void deleteCategory(Long categoryId);
}
