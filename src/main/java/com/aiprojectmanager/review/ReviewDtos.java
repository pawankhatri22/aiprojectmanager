package com.aiprojectmanager.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ReviewDtos {
    public record CreateReviewRequest(@NotNull Long sessionId, @NotNull @Min(1) @Max(5) Integer rating, @NotBlank String comment) {}
    public record ReviewResponse(Long id, Long sessionId, Long mentorId, Long graduateId, Integer rating, String comment, LocalDateTime createdAt) {}
}
