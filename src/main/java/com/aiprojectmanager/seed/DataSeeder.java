package com.aiprojectmanager.seed;

import com.aiprojectmanager.profile.*;
import com.aiprojectmanager.user.Role;
import com.aiprojectmanager.user.User;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final GraduateProfileRepository graduateProfileRepository;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepository, MentorProfileRepository mentorProfileRepository,
                      GraduateProfileRepository graduateProfileRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.graduateProfileRepository = graduateProfileRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail("admin@local.com").orElseGet(() ->
                userRepository.save(User.builder().email("admin@local.com").password(encoder.encode("admin123"))
                        .role(Role.ADMIN).enabled(true).createdAt(LocalDateTime.now()).build())
        );

        User mentor = userRepository.findByEmail("khatri.pawankumar22@gmail.com").orElseGet(() ->
                userRepository.save(User.builder().email("khatri.pawankumar22@gmail.com").password(encoder.encode("Mentor@123"))
                        .role(Role.MENTOR).enabled(true).createdAt(LocalDateTime.now()).build())
        );

        User student = userRepository.findByEmail("khatri.catten22@gmail.com").orElseGet(() ->
                userRepository.save(User.builder().email("khatri.catten22@gmail.com").password(encoder.encode("Student@123"))
                        .role(Role.GRADUATE).enabled(true).createdAt(LocalDateTime.now()).build())
        );

        mentorProfileRepository.findByUserId(mentor.getId()).orElseGet(() -> {
            MentorProfile profile = MentorProfile.builder()
                    .user(mentor)
                    .fullName("Pawan Kumar Khatri")
                    .company("Fintech Systems")
                    .jobTitle("Lead Full Stack Developer")
                    .yearsExperience(11)
                    .bio("Experienced Senior Java Developer / Lead Full Stack Developer with extensive expertise in designing and building scalable fintech and enterprise applications. Skilled in Java, Spring Boot, Kafka, microservices architecture, and integrating multiple payment APIs. Strong experience in analytics systems, VR-based user activity tracking, and real-time data processing. Proven track record in leading development, optimizing system performance, and delivering reliable solutions with a focus on maintainability and scalability. Experienced with Angular for frontend development, PostgreSQL, ClickHouse, and cloud deployment (AWS/Azure).")
                    .hourlyRate(BigDecimal.valueOf(80))
                    .ratingAverage(4.8)
                    .photoUrl("https://images.unsplash.com/photo-1568602471122-7832951cc4c5")
                    .build();
            List<String> skills = List.of("Java", "Spring Boot", "Microservices", "Kafka", "REST APIs", "Payment Gateway Integration",
                    "Fintech Applications", "Angular", "PostgreSQL", "ClickHouse", "Redis", "Docker", "AWS", "Azure", "H2 Database",
                    "Batch Processing", "Asynchronous Processing", "System Scalability", "Analytics Systems", "VR User Activity Tracking",
                    "3D Heatmaps", "Data Processing", "Performance Optimization", "Git", "Maven", "JUnit", "Agile Methodologies", "Team Leadership");
            skills.forEach(x -> profile.getExpertise().add(MentorExpertise.builder().mentorProfile(profile).tag(x).build()));
            return mentorProfileRepository.save(profile);
        });

        graduateProfileRepository.findByUserId(student.getId()).orElseGet(() -> {
            GraduateProfile profile = GraduateProfile.builder()
                    .user(student)
                    .fullName("Catten Khatri")
                    .education("B.Tech Computer Science")
                    .careerGoal("Become a strong backend engineer in fintech domain")
                    .resumeUrl("https://example.com/resume/catten.pdf")
                    .build();
            List<String> skills = List.of("Java Basics", "Spring", "SQL", "Angular", "Git");
            skills.forEach(s -> profile.getSkills().add(GraduateSkill.builder().graduateProfile(profile).skill(s).build()));
            return graduateProfileRepository.save(profile);
        });
    }
}
