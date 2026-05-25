package ru.persea.userservice.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.persea.userservice.dto.BrandDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandSyncHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BrandSyncHandler handler;

    private ObjectMapper realMapper;

    @BeforeEach
    void setUp() {
        realMapper = new ObjectMapper();
    }

    private JsonNode buildMessage(String op, String payload) throws Exception {
        String json = String.format("""
                {"payload": {"op": "%s", "after": %s, "before": %s}}
                """, op,
                op.equals("d") ? "null" : payload,
                op.equals("d") ? payload : "null"
        );
        return realMapper.readTree(json);
    }

    @Test
    @DisplayName("consumeProductSync — null message игнорируется")
    void nullMessage_ignored() {
        handler.consumeProductSync(null);
        verify(productService, never()).syncBrand(any());
        verify(productService, never()).deleteBrand(any());
    }

    @Test
    @DisplayName("consumeProductSync — op=c вызывает syncBrand")
    void opCreate_callsSyncBrand() throws Exception {
        JsonNode message = buildMessage("c", """
                {"id": 1, "name": "Nike"}
                """);
        BrandDto dto = new BrandDto(1L, "Nike");
        when(objectMapper.convertValue(any(JsonNode.class), eq(BrandDto.class))).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncBrand(dto);
        verify(productService, never()).deleteBrand(any());
    }

    @Test
    @DisplayName("consumeProductSync — op=u вызывает syncBrand")
    void opUpdate_callsSyncBrand() throws Exception {
        JsonNode message = buildMessage("u", """
                {"id": 2, "name": "Adidas"}
                """);
        BrandDto dto = new BrandDto(2L, "Adidas");
        when(objectMapper.convertValue(any(JsonNode.class), eq(BrandDto.class))).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncBrand(dto);
    }

    @Test
    @DisplayName("consumeProductSync — op=r вызывает syncBrand")
    void opRead_callsSyncBrand() throws Exception {
        JsonNode message = buildMessage("r", """
                {"id": 3, "name": "Puma"}
                """);
        BrandDto dto = new BrandDto(3L, "Puma");
        when(objectMapper.convertValue(any(JsonNode.class), eq(BrandDto.class))).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).syncBrand(dto);
    }

    @Test
    @DisplayName("consumeProductSync — op=d вызывает deleteBrand с корректным id")
    void opDelete_callsDeleteBrand() throws Exception {
        JsonNode message = buildMessage("d", """
                {"id": 5, "name": "Reebok"}
                """);
        BrandDto dto = new BrandDto(5L, "Reebok");
        when(objectMapper.convertValue(any(JsonNode.class), eq(BrandDto.class))).thenReturn(dto);

        handler.consumeProductSync(message);

        verify(productService).deleteBrand(5L);
        verify(productService, never()).syncBrand(any());
    }

    @Test
    @DisplayName("consumeProductSync — неизвестный op не вызывает никаких методов сервиса")
    void unknownOp_callsNothing() throws Exception {
        String json = """
                {"payload": {"op": "x", "after": {"id": 1, "name": "X"}, "before": null}}
                """;
        JsonNode message = realMapper.readTree(json);

        handler.consumeProductSync(message);

        verify(productService, never()).syncBrand(any());
        verify(productService, never()).deleteBrand(any());
    }
}