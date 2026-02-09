package com.aiprojectmanager.profile;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public class ProfileDtos {
    public record MentorProfileRequest(@NotBlank String fullName, String company, String jobTitle, Integer yearsExperience,
                                       String bio, BigDecimal hourlyRate, String photoUrl, List<String> expertise) {}
    public record MentorProfileResponse(Long id, Long userId, String fullName, String company, String jobTitle,
                                        Integer yearsExperience, String bio, BigDecimal hourlyRate, Double ratingAverage,
                                        String photoUrl, List<String> expertise) {}

    public record GraduateProfileRequest(@NotBlank String fullName, String education, String careerGoal,
                                         String resumeUrl, List<String> skills) {}
    public record GraduateProfileResponse(Long id, Long userId, String fullName, String education,
                                          String careerGoal, String resumeUrl, List<String> skills) {}
}
