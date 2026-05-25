package ru.persea.userservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.dto.ProductSyncDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class ProductSyncHandler {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${kafka.topics.product-cdc}",
        groupId = "${kafka.consumer-groups.product-sync}",
        containerFactory = "jsonNodeKafkaListenerContainerFactory"
    )
    public void consumeProductSync(@Payload(required = false) JsonNode message) {
        if (message == null || message.isNull() || message.isEmpty()) return;
        
        String op = message.get("payload").get("op").asText();
        JsonNode after = message.get("payload").get("after");
        JsonNode before = message.get("payload").get("before");

        switch (op) {
            case "c", "r", "u" -> {
                var dto = objectMapper.convertValue(after, ProductSyncDto.class);
                productService.syncProduct(dto);
            }
            case "d" -> {
                var dto = objectMapper.convertValue(before, ProductSyncDto.class);
                productService.deleteProduct(dto.id());
            }
        }
    }
}
