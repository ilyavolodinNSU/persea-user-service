package ru.persea.userservice.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.service.UserService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserActionsEventHandlerTest {

    private UserService userService;
    private ObjectMapper mockMapper;
    private UserActionsEventHandler handler;
    private ObjectMapper realMapper;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMapper  = mock(ObjectMapper.class);
        realMapper  = new ObjectMapper();
        handler     = new UserActionsEventHandler(userService, mockMapper);

        // Диагностика — если упадёт здесь, проблема в конструкторе
        assertNotNull(userService, "userService is null");
        assertNotNull(mockMapper,  "mockMapper is null");
        assertNotNull(handler,     "handler is null");
    }

    @Test
    void consumeProductViewed_nullMessage_doesNothing() {
        handler.consumeProductViewed(null);
        verifyNoInteractions(userService);
    }

    @Test
    void consumeProductViewed_nullJsonNode_doesNothing() throws Exception {
        JsonNode message = realMapper.readTree("null");
        handler.consumeProductViewed(message);
        verifyNoInteractions(userService);
    }

    @Test
    void consumeProductViewed_emptyJsonNode_doesNothing() throws Exception {
        JsonNode message = realMapper.readTree("{}");
        handler.consumeProductViewed(message);
        verifyNoInteractions(userService);
    }

    @Test
    void consumeProductViewed_validMessage_callsSyncAction() throws Exception {
        String payloadStr = "{\"user_id\":\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\",\"product_id\":2,\"type\":\"VIEW\",\"created_at\":\"2024-01-01T00:00:00Z\"}";
        String json = """
                {
                  "payload": "%s"
                }
                """.formatted(payloadStr.replace("\"", "\\\""));

        JsonNode message = realMapper.readTree(json);

        UserActionEvent event = UserActionEvent.builder()
                .userId(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .productId(2L)
                .type("VIEW")
                .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
                .build();

        when(mockMapper.readValue(payloadStr, UserActionEvent.class)).thenReturn(event);

        handler.consumeProductViewed(message);

        verify(userService).syncAction(event);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void consumeProductViewed_invalidPayload_throwsRuntimeException() throws Exception {
        String payloadStr = "invalid-json";
        String json = """
                {
                  "payload": "invalid-json"
                }
                """;

        JsonNode message = realMapper.readTree(json);

        when(mockMapper.readValue(payloadStr, UserActionEvent.class))
                .thenThrow(mock(JacksonException.class));

        assertThrows(RuntimeException.class, () -> handler.consumeProductViewed(message));
        verifyNoInteractions(userService);
    }
}
