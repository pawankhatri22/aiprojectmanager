package com.aiprojectmanager.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDtos {
    public record PaymentResponse(Long id, Long sessionId, BigDecimal amount, String currency,
                                  PaymentStatus status, String transactionReference, LocalDateTime timestamp) {}
}
