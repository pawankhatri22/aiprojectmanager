package com.aiprojectmanager.notification;

import java.time.LocalDateTime;

public class NotificationDtos {
    public record NotificationResponse(Long id, NotificationType type, String message, boolean read, LocalDateTime createdAt) {}
}
