package ru.persea.userservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import ru.persea.userservice.dto.UserActionEvent;

@Component
public class UserActionsEventHandler {

    @KafkaListener(
        topics = "user-actions", 
        groupId = "user-actions-sink",
        containerFactory = "userActionsKafkaListenerContainerFactory"
    )
    public void handle(UserActionEvent event) {
        System.out.println(event);
    }
}
