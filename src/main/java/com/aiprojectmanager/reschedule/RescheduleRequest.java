package com.aiprojectmanager.reschedule;

import com.aiprojectmanager.session.Session;
import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RescheduleRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Session session;

    @ManyToOne(optional = false)
    private User requestedBy;

    private LocalDateTime oldScheduledTime;
    private Integer oldDurationMinutes;
    private LocalDateTime newScheduledTime;
    private Integer newDurationMinutes;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    private RescheduleStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime decidedAt;
}
