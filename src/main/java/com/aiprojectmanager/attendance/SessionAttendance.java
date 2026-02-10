package com.aiprojectmanager.attendance;

import com.aiprojectmanager.session.Session;
import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionAttendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Session session;

    @ManyToOne(optional = false)
    private User participant;

    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private LocalDateTime updatedAt;
}
