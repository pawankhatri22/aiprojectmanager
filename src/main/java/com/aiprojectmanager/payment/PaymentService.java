package com.aiprojectmanager.payment;

import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.session.SessionService;
import com.aiprojectmanager.session.SessionStatus;
import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final SessionService sessionService;
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;

    public PaymentService(PaymentRepository repository, SessionService sessionService, SessionRepository sessionRepository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
    }

    public PaymentDtos.PaymentResponse pay(Long sessionId) {
        var user = currentUserService.get();
        if (user.getRole() != Role.GRADUATE) throw new IllegalArgumentException("Graduate role required");

        var updatedSession = sessionService.markPaid(sessionId);
        if (updatedSession.status() != SessionStatus.PAID) throw new IllegalArgumentException("Session payment could not be completed");

        var session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        var existing = repository.findBySessionId(sessionId).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.PAID) {
            return toDto(existing);
        }

        Payment payment = existing == null ? new Payment() : existing;
        payment.setSession(session);
        payment.setAmount(updatedSession.price());
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionReference("TXN-" + UUID.randomUUID());
        payment.setTimestamp(LocalDateTime.now());
        return toDto(repository.save(payment));
    }

    public Page<PaymentDtos.PaymentResponse> my(Pageable pageable) {
        var user = currentUserService.get();
        return repository.findBySessionGraduateIdOrSessionMentorId(user.getId(), user.getId(), pageable).map(this::toDto);
    }

    private PaymentDtos.PaymentResponse toDto(Payment p) {
        return new PaymentDtos.PaymentResponse(
                p.getId(), p.getSession().getId(), p.getAmount(), p.getCurrency(),
                p.getStatus(), p.getTransactionReference(), p.getTimestamp()
        );
    }
}
