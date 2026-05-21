package ru.persea.userservice.service;

import ru.persea.userservice.dto.ProductSyncDto;

public interface ProductService {
    public void syncProduct(ProductSyncDto product);

    public void deleteProduct(Long productId); 
}
