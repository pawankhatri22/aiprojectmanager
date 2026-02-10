package com.aiprojectmanager.messaging;

import com.aiprojectmanager.session.Session;
import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Session session;

    @ManyToOne(optional = false)
    private User sender;

    @Column(length = 2000)
    private String messageText;
    private LocalDateTime timestamp;
}
