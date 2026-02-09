package com.aiprojectmanager.session;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SessionDtos {
    public record RequestSessionRequest(@NotNull Long mentorId, @Future LocalDateTime scheduledTime, @Min(15) Integer durationMinutes) {}
    public record SessionResponse(Long id, Long mentorId, Long graduateId, LocalDateTime scheduledTime,
                                  Integer durationMinutes, BigDecimal price, SessionStatus status) {}
}
