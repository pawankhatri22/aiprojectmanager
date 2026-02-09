package com.aiprojectmanager.payment;

import com.aiprojectmanager.session.Session;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Session session;

    private BigDecimal amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionReference;
    private LocalDateTime timestamp;
}
