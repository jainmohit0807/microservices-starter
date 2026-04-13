package com.mohitjain.notification.service;

import com.mohitjain.notification.dto.NotificationEvent;
import com.mohitjain.notification.entity.NotificationLog;
import com.mohitjain.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final NotificationLogRepository logRepository;

    @Async
    public void sendSms(NotificationEvent event) {
        String phone = event.getPhone();
        if (phone == null || phone.isBlank()) {
            log.debug("No phone number for event {}, skipping SMS", event.getType());
            return;
        }

        log.info("Sending SMS to {}", phone);

        try {
            // In production: use Twilio/AWS SNS client
            log.info("SMS sent to {} for event {}", phone, event.getType());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("SMS")
                    .recipient(phone)
                    .status("SENT")
                    .payload(event.getMessage() != null ? event.getMessage() : "Notification sent")
                    .build());

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phone, e.getMessage());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("SMS")
                    .recipient(phone)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .build());
        }
    }
}
