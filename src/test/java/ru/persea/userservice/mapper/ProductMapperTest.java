package ru.persea.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.BrandEntity;
import ru.persea.userservice.entity.CategoryEntity;
import ru.persea.userservice.entity.ProductEntity;

import static org.assertj.core.api.Assertions.assertThat;


class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("toDto(CategoryEntity) — все поля маппируются корректно")
    void categoryEntityToDto() {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(1L);
        entity.setName("Beverages");

        CategoryDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Beverages");
    }

    @Test
    @DisplayName("toEntity(CategoryDto) — все поля маппируются корректно")
    void categoryDtoToEntity() {
        CategoryDto dto = new CategoryDto(2L, "Dairy", "N232");

        CategoryEntity entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getName()).isEqualTo("Dairy");
        assertThat(entity.getCode()).isEqualTo("N232");
    }

    @Test
    @DisplayName("toDto(CategoryEntity) — null возвращает null")
    void categoryEntityToDto_null() {
        assertThat(mapper.toDto((CategoryEntity) null)).isNull();
    }

    // BrandEntity <-> BrandDto

    @Test
    @DisplayName("toDto(BrandEntity) — все поля маппируются корректно")
    void brandEntityToDto() {
        BrandEntity entity = new BrandEntity();
        entity.setId(10L);
        entity.setName("Nike");

        BrandDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.name()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("toEntity(BrandDto) — все поля маппируются корректно")
    void brandDtoToEntity() {
        BrandDto dto = new BrandDto(20L, "Adidas");

        BrandEntity entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getName()).isEqualTo("Adidas");
    }

    @Test
    @DisplayName("toDto(BrandEntity) — null возвращает null")
    void brandEntityToDto_null() {
        assertThat(mapper.toDto((BrandEntity) null)).isNull();
    }

    // ProductEntity <-> ProductDto

    @Test
    @DisplayName("toDto(ProductEntity) — все поля маппируются корректно")
    void productEntityToDto() {
        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setName("Beverages");

        BrandEntity brand = new BrandEntity();
        brand.setId(10L);
        brand.setName("Nike");

        ProductEntity entity = new ProductEntity();
        entity.setId(100L);
        entity.setName("Product X");
        entity.setRating(4);
        entity.setImageURI("http://img.example.com/x.jpg");
        entity.setCategory(category);
        entity.setBrand(brand);

        ProductDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.name()).isEqualTo("Product X");
        assertThat(dto.rating()).isEqualTo(4);
        assertThat(dto.imageURI()).isEqualTo("http://img.example.com/x.jpg");
        assertThat(dto.category().id()).isEqualTo(1L);
        assertThat(dto.brand().id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("toEntity(ProductDto) — все поля маппируются корректно")
    void productDtoToEntity() {
        ProductDto dto = new ProductDto(
                100L, "Product X",
                new BrandDto(10L, "Nike"),
                new CategoryDto(1L, "Beverages", "B32"),
                4, "http://img.example.com/x.jpg"
        );

        ProductEntity entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(100L);
        assertThat(entity.getName()).isEqualTo("Product X");
        assertThat(entity.getRating()).isEqualTo(4);
        assertThat(entity.getBrand().getId()).isEqualTo(10L);
        assertThat(entity.getCategory().getId()).isEqualTo(1L);
        assertThat(entity.getCategory().getCode()).isEqualTo("B32");
    }

    @Test
    @DisplayName("toDto(ProductEntity) — null возвращает null")
    void productEntityToDto_null() {
        assertThat(mapper.toDto((ProductEntity) null)).isNull();
    }
}