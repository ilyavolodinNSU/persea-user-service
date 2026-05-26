package ru.persea.userservice.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;

class ProductSyncHandlerTest {

    private ProductService productService;
    private ObjectMapper mockMapper;
    private ProductSyncHandler handler;
    private ObjectMapper realMapper;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMapper     = mock(ObjectMapper.class);
        realMapper     = new ObjectMapper();
        handler        = new ProductSyncHandler(productService, mockMapper);
    }

    @Test
    void consumeProductSync_nullMessage_doesNothing() {
        handler.consumeProductSync(null);
        verifyNoInteractions(productService);
    }

    @Test
    void consumeProductSync_createOp_callsSyncProduct() throws Exception {
        String json = """
                {
                  "payload": {
                    "op": "c",
                    "after": {
                      "id": 1,
                      "name": "Laptop",
                      "brand_id": 5,
                      "category_id": 10,
                      "rating": 4,
                      "image_uri": "https://example.com/laptop.jpg"
                    },
                    "before": null
                  }
                }
                """;
        JsonNode message = realMapper.readTree(json);
        JsonNode after   = message.get("payload").get("after");

        ProductSyncDto dto = new ProductSyncDto(1L, "Laptop", 5L, 10L, 4, "https://example.com/laptop.jpg");
        when(mockMapper.convertValue(after, ProductSyncDto.class)).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncProduct(dto);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void consumeProductSync_updateOp_callsSyncProduct() throws Exception {
        String json = """
                {
                  "payload": {
                    "op": "u",
                    "after": {
                      "id": 2,
                      "name": "Mouse",
                      "brand_id": 6,
                      "category_id": 11,
                      "rating": 3,
                      "image_uri": "https://example.com/mouse.jpg"
                    },
                    "before": {
                      "id": 2,
                      "name": "Old Mouse",
                      "brand_id": 6,
                      "category_id": 11,
                      "rating": 2,
                      "image_uri": "https://example.com/old-mouse.jpg"
                    }
                  }
                }
                """;
        JsonNode message = realMapper.readTree(json);
        JsonNode after   = message.get("payload").get("after");

        ProductSyncDto dto = new ProductSyncDto(2L, "Mouse", 6L, 11L, 3, "https://example.com/mouse.jpg");
        when(mockMapper.convertValue(after, ProductSyncDto.class)).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncProduct(dto);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void consumeProductSync_deleteOp_callsDeleteProduct() throws Exception {
        String json = """
                {
                  "payload": {
                    "op": "d",
                    "after": null,
                    "before": {
                      "id": 3,
                      "name": "Keyboard",
                      "brand_id": 7,
                      "category_id": 12,
                      "rating": 5,
                      "image_uri": "https://example.com/keyboard.jpg"
                    }
                  }
                }
                """;
        JsonNode message = realMapper.readTree(json);
        JsonNode before  = message.get("payload").get("before");

        ProductSyncDto dto = new ProductSyncDto(3L, "Keyboard", 7L, 12L, 5, "https://example.com/keyboard.jpg");
        when(mockMapper.convertValue(before, ProductSyncDto.class)).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).deleteProduct(3L);
        verifyNoMoreInteractions(productService);
    }
}
