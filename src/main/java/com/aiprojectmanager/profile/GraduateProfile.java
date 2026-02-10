package com.aiprojectmanager.profile;

import com.aiprojectmanager.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GraduateProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private User user;

    private String fullName;
    private String education;
    private String careerGoal;
    private String resumeUrl;

    @OneToMany(mappedBy = "graduateProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GraduateSkill> skills = new ArrayList<>();
}
