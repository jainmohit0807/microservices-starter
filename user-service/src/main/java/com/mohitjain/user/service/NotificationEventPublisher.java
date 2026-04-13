package com.mohitjain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohitjain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private static final String NOTIFICATION_TOPIC = "notification-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserRegistered(User user) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "type", "USER_REGISTERED",
                    "email", user.getEmail(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "userId", user.getId()
            ));
            kafkaTemplate.send(NOTIFICATION_TOPIC, user.getEmail(), payload);
            log.info("Published USER_REGISTERED event for {}", user.getEmail());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish notification event for user {}: {}",
                    user.getEmail(), e.getMessage());
        }
    }
}
