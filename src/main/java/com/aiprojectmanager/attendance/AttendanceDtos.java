package com.aiprojectmanager.attendance;

import java.time.LocalDateTime;

public class AttendanceDtos {
    public record AttendanceResponse(Long id, Long sessionId, Long participantId, String participantEmail,
                                     LocalDateTime joinTime, LocalDateTime leaveTime, AttendanceStatus status, LocalDateTime updatedAt) {}
}
