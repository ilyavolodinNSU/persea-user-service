package ru.persea.userservice.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.CategoryDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CategoriesSyncHandler {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(
        topics = "${kafka.topics.product-categories-cdc}",
        groupId = "${kafka.consumer-groups.product-categories-sync}",
        containerFactory = "jsonNodeKafkaListenerContainerFactory"
    )
    public void consumeProductSync(@Payload(required = false) JsonNode message) {
        if (message == null || message.isNull() || message.isEmpty()) return;
        
        String op = message.get("payload").get("op").asString();
        JsonNode after = message.get("payload").get("after");
        JsonNode before = message.get("payload").get("before");

        switch (op) {
            case "c", "r", "u" -> {
                var dto = objectMapper.convertValue(after, CategoryDto.class);
                productService.syncCategory(dto);
            }
            case "d" -> {
                var dto = objectMapper.convertValue(before, CategoryDto.class);
                productService.deleteCategory(dto.id());
            }
        }
    }
}