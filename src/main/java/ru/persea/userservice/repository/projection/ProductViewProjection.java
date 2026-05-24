package ru.persea.userservice.repository.projection;

public interface ProductViewProjection {
    Long getId();
    String getName();
    
    Long getBrandId();
    String getBrandName();
    
    Long getCategoryId();
    String getCategoryName();
    String getCategoryCode();
    
    Integer getRating();
    String getImageUri();
}
