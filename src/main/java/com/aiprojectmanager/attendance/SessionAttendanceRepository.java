package com.aiprojectmanager.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionAttendanceRepository extends JpaRepository<SessionAttendance, Long> {
    Optional<SessionAttendance> findBySessionIdAndParticipantEmail(Long sessionId, String email);
    List<SessionAttendance> findBySessionId(Long sessionId);
}
