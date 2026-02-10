package com.aiprojectmanager.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findBySessionGraduateIdOrSessionMentorId(Long graduateId, Long mentorId, Pageable pageable);
    Optional<Payment> findBySessionId(Long sessionId);
}
