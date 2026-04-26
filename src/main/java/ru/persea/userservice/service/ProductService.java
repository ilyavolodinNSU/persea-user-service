package ru.persea.userservice.service;

import ru.persea.userservice.dto.ProductDto;

public interface ProductService {
    public void addProduct(ProductDto product);

    public void deleteProduct(Long productId); 
}
