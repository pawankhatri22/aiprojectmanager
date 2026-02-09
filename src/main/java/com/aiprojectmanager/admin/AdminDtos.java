package com.aiprojectmanager.admin;

import com.aiprojectmanager.payment.PaymentStatus;
import com.aiprojectmanager.session.SessionStatus;
import com.aiprojectmanager.user.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminDtos {
    public record UserAdminResponse(Long id, String email, Role role, boolean enabled, LocalDateTime createdAt) {}
    public record SessionAdminResponse(Long id, Long mentorId, Long graduateId, LocalDateTime scheduledTime,
                                       Integer durationMinutes, BigDecimal price, SessionStatus status) {}
    public record PaymentAdminResponse(Long id, Long sessionId, BigDecimal amount, String currency,
                                       PaymentStatus status, String transactionReference, LocalDateTime timestamp) {}
}
