package com.mohitjain.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohitjain.notification.service.EmailService;
import com.mohitjain.notification.service.SmsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NotificationListener listener;

    @Test
    void onNotificationEvent_userRegistered_sendsEmailAndSms() {
        String payload = """
                {
                  "type": "USER_REGISTERED",
                  "email": "user@test.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "userId": 1
                }
                """;

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "notification-events", 0, 1L, "user@test.com", payload);

        listener.onNotificationEvent(record);

        verify(emailService).sendWelcomeEmail(any());
        verify(smsService).sendSms(any());
    }

    @Test
    void onNotificationEvent_passwordReset_sendsResetEmail() {
        String payload = """
                {
                  "type": "PASSWORD_RESET",
                  "email": "user@test.com",
                  "firstName": "John",
                  "lastName": "Doe",
                  "userId": 1
                }
                """;

        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "notification-events", 0, 3L, "user@test.com", payload);

        listener.onNotificationEvent(record);

        verify(emailService).sendPasswordResetEmail(any());
    }

    @Test
    void onNotificationEvent_invalidJson_doesNotThrow() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "notification-events", 0, 2L, "key", "invalid-json");

        listener.onNotificationEvent(record);
    }
}
