package com.aiprojectmanager.profile;

import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private User user;

    private String fullName;
    private String company;
    private String jobTitle;
    private Integer yearsExperience;
    @Column(length = 1000)
    private String bio;
    private BigDecimal hourlyRate;
    private Double ratingAverage;
    private String photoUrl;

    @OneToMany(mappedBy = "mentorProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MentorExpertise> expertise = new ArrayList<>();
}
