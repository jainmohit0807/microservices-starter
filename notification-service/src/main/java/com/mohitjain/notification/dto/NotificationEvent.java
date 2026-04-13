package com.mohitjain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String type;

    private String email;

    private String firstName;

    private String lastName;

    private Long userId;

    private String phone;

    private String message;
}
