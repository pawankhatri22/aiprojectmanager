package com.aiprojectmanager.reschedule;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class RescheduleDtos {
    public record CreateRescheduleRequest(@NotNull @Future LocalDateTime newScheduledTime, @NotNull @Min(15) Integer newDurationMinutes, @NotBlank String reason) {}
    public record RescheduleResponse(Long id, Long sessionId, Long requestedBy, LocalDateTime oldScheduledTime, Integer oldDurationMinutes,
                                     LocalDateTime newScheduledTime, Integer newDurationMinutes, String reason,
                                     RescheduleStatus status, LocalDateTime createdAt, LocalDateTime decidedAt) {}
}
