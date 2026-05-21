package ru.persea.userservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.persea.userservice.dto.UserActionEvent;
import ru.persea.userservice.service.UserService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class UserActionsEventHandler {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${kafka.topics.product-outbox}",
        groupId = "${kafka.consumer-groups.product-viewed}",
        containerFactory = "jsonNodeKafkaListenerContainerFactory"
    )
    public void consumeProductViewed(JsonNode message) {
        String payloadStr = message.get("payload").asString();
        var event = objectMapper.readValue(payloadStr, UserActionEvent.class);
        userService.saveAction(event);
    }
}
