package com.aiprojectmanager.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MessageDtos {
    public record SendMessageRequest(@NotNull Long sessionId, @NotBlank String messageText) {}
    public record MessageResponse(Long id, Long sessionId, Long senderId, String messageText, LocalDateTime timestamp) {}
}
