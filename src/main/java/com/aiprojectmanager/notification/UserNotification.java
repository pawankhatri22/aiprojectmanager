package com.aiprojectmanager.notification;

import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(length = 1000)
    private String message;

    private boolean read;
    private LocalDateTime createdAt;
}
