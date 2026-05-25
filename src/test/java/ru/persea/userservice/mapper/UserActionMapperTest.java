package ru.persea.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.entity.UserActionEntity;
import ru.persea.userservice.entity.UserActionTypeEntity;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserActionMapperImpl.class})
class UserActionMapperTest {

    @Autowired
    private UserActionMapper mapper;

    @Test
    void toViewedProductDto_mapsIdFromEntityId() {
        UserActionTypeEntity type = new UserActionTypeEntity(1L, "VIEW");

        UserActionEntity entity = UserActionEntity.builder()
                .id(42L)
                .userId(UUID.randomUUID())
                .productId(99L)
                .type(type)
                .createdAt(Instant.now())
                .build();

        ProductDto dto = mapper.toViewedProductDto(entity);

        assertThat(dto).isNotNull();
        // MapStruct маппит id -> id напрямую
        assertThat(dto.id()).isEqualTo(42L);
        // Остальные поля не имеют соответствия в UserActionEntity — будут null
        assertThat(dto.name()).isNull();
        assertThat(dto.brand()).isNull();
        assertThat(dto.category()).isNull();
        assertThat(dto.rating()).isNull();
        assertThat(dto.imageURI()).isNull();
    }

    @Test
    void toViewedProductDto_withNullEntity_returnsNull() {
        ProductDto dto = mapper.toViewedProductDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toViewedProductDto_withMinimalEntity_doesNotThrow() {
        UserActionEntity entity = new UserActionEntity();
        entity.setId(1L);

        ProductDto dto = mapper.toViewedProductDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
    }
}
