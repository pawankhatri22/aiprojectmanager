package com.aiprojectmanager.payment;

import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.user.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;

    public PaymentService(PaymentRepository repository, SessionRepository sessionRepository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
    }

    public PaymentDtos.PaymentResponse pay(Long sessionId) {
        var session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        var payment = repository.save(Payment.builder().session(session).amount(session.getPrice()).currency("USD")
                .status(PaymentStatus.PAID).transactionReference("TXN-" + UUID.randomUUID()).timestamp(LocalDateTime.now()).build());
        return toDto(payment);
    }

    public Page<PaymentDtos.PaymentResponse> my(Pageable pageable) {
        var user = currentUserService.get();
        return repository.findBySessionGraduateIdOrSessionMentorId(user.getId(), user.getId(), pageable).map(this::toDto);
    }

    private PaymentDtos.PaymentResponse toDto(Payment p) {
        return new PaymentDtos.PaymentResponse(p.getId(), p.getSession().getId(), p.getAmount(), p.getCurrency(), p.getStatus(), p.getTransactionReference(), p.getTimestamp());
    }
}
