package ru.persea.userservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.ProductDto;
import ru.persea.userservice.service.ProductService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CdcHandler {
    private final ProductService productService;
    private final ObjectMapper mapper;

    @KafkaListener(
        topics = "data.cdc.public.products", 
        groupId = "products-sync",
        containerFactory = "cdcKafkaListenerContainerFactory"
    )
    public void handle(JsonNode event) {
        JsonNode payload = event.get("payload");
        String op = payload.get("op").asString();
        JsonNode after = payload.get("after");
        JsonNode before = payload.get("before");

        switch(op) {
            case "c", "r", "u" -> productService.addProduct(mapper.treeToValue(after, ProductDto.class));
            case "d" -> productService.deleteProduct(before.get("id").asLong());
        }
    }
}
