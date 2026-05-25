package ru.persea.userservice.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;

class CategoriesSyncHandlerTest {

    private ProductService productService;
    private ObjectMapper mockMapper;
    private CategoriesSyncHandler handler;
    private ObjectMapper realMapper;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMapper     = mock(ObjectMapper.class);
        realMapper     = new ObjectMapper();
        // Порядок аргументов конструктора совпадает с порядком полей в классе:
        // private final ObjectMapper objectMapper;
        // private final ProductService productService;
        handler = new CategoriesSyncHandler(mockMapper, productService);
    }

    @Test
    void consumeProductSync_nullMessage_doesNothing() {
        handler.consumeProductSync(null);
        verifyNoInteractions(productService);
    }

    @Test
    void consumeProductSync_createOp_callsSyncCategory() throws Exception {
        String json = """
                {
                  "payload": {
                    "op": "c",
                    "after": { "id": 1, "name": "Electronics", "code": "ELEC" },
                    "before": null
                  }
                }
                """;
        JsonNode message = realMapper.readTree(json);
        JsonNode after   = message.get("payload").get("after");

        CategoryDto dto = new CategoryDto(1L, "Electronics", "ELEC");
        when(mockMapper.convertValue(after, CategoryDto.class)).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncCategory(dto);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void consumeProductSync_deleteOp_callsDeleteCategory() throws Exception {
        String json = """
                {
                  "payload": {
                    "op": "d",
                    "after": null,
                    "before": { "id": 3, "name": "ToDelete", "code": "DEL" }
                  }
                }
                """;
        JsonNode message = realMapper.readTree(json);
        JsonNode before  = message.get("payload").get("before");

        CategoryDto dto = new CategoryDto(3L, "ToDelete", "DEL");
        when(mockMapper.convertValue(before, CategoryDto.class)).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).deleteCategory(3L);
        verifyNoMoreInteractions(productService);
    }
}
