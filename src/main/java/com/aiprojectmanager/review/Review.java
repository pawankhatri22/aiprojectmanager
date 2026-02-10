package com.aiprojectmanager.review;

import com.aiprojectmanager.session.Session;
import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Session session;

    @ManyToOne(optional = false)
    private User mentor;

    @ManyToOne(optional = false)
    private User graduate;

    private Integer rating;

    @Column(length = 2000)
    private String comment;

    private LocalDateTime createdAt;
}
