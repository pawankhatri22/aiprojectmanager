package com.aiprojectmanager.session;

import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Session {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User mentor;

    @ManyToOne(optional = false)
    private User graduate;

    private LocalDateTime scheduledTime;
    private Integer durationMinutes;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private String confirmationLink;
    private String meetingLink;
    private LocalDateTime createdAt;
}
