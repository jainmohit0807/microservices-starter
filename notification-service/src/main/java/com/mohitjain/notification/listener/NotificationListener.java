package com.mohitjain.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohitjain.notification.dto.NotificationEvent;
import com.mohitjain.notification.service.EmailService;
import com.mohitjain.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void onNotificationEvent(ConsumerRecord<String, String> record) {
        log.info("Received notification event: key={} from {}[{}]@{}",
                record.key(), record.topic(), record.partition(), record.offset());

        try {
            NotificationEvent event = objectMapper.readValue(record.value(), NotificationEvent.class);

            switch (event.getType()) {
                case "USER_REGISTERED" -> {
                    emailService.sendWelcomeEmail(event);
                    smsService.sendSms(event);
                }
                case "PASSWORD_RESET" -> emailService.sendPasswordResetEmail(event);
                default -> log.warn("Unknown event type: {}", event.getType());
            }

        } catch (Exception e) {
            log.error("Failed to process notification event: {}", e.getMessage(), e);
        }
    }
}
