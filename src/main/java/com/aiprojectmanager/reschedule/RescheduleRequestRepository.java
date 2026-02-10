package com.aiprojectmanager.reschedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RescheduleRequestRepository extends JpaRepository<RescheduleRequest, Long> {
    List<RescheduleRequest> findBySessionIdOrderByCreatedAtDesc(Long sessionId);
}
