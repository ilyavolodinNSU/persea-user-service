package ru.persea.userservice.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import ru.persea.userservice.dto.UserActionEvent;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Bean
    public ConsumerFactory<String, JsonNode> cdcConsumerFactory() {
        JacksonJsonDeserializer<JsonNode> deserializer = new JacksonJsonDeserializer<>(JsonNode.class, new JsonMapper());

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "products-sync");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonNode> cdcKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, JsonNode> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cdcConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserActionEvent> userActionsConsumerFactory() {
        JacksonJsonDeserializer<UserActionEvent> deserializer = new JacksonJsonDeserializer<>(UserActionEvent.class);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "user-actions-sink");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
                config.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, "ru.persea.userservice.dto.UserActionEvent");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserActionEvent> userActionsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserActionEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userActionsConsumerFactory());
        return factory;
    }

}
