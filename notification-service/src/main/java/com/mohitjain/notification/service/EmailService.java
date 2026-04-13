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
public class EmailService {

    private final NotificationLogRepository logRepository;

    @Async
    public void sendPasswordResetEmail(NotificationEvent event) {
        log.info("Sending password reset email to {}", event.getEmail());

        try {
            log.info("Password reset email sent to {}", event.getEmail());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("EMAIL")
                    .recipient(event.getEmail())
                    .status("SENT")
                    .payload("Password reset email for user " + event.getUserId())
                    .build());

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", event.getEmail(), e.getMessage());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("EMAIL")
                    .recipient(event.getEmail())
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .build());
        }
    }

    @Async
    public void sendWelcomeEmail(NotificationEvent event) {
        log.info("Sending welcome email to {}", event.getEmail());

        try {
            // In production: use JavaMailSender or SendGrid/SES client
            log.info("Welcome email sent to {} ({} {})",
                    event.getEmail(), event.getFirstName(), event.getLastName());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("EMAIL")
                    .recipient(event.getEmail())
                    .status("SENT")
                    .payload("Welcome email for user " + event.getUserId())
                    .build());

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getEmail(), e.getMessage());

            logRepository.save(NotificationLog.builder()
                    .eventType(event.getType())
                    .channel("EMAIL")
                    .recipient(event.getEmail())
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .build());
        }
    }
}
