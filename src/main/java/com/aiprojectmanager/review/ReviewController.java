package com.aiprojectmanager.review;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDtos.ReviewResponse>> create(@Valid @RequestBody ReviewDtos.CreateReviewRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(reviewService.create(req), "Review submitted"));
    }

    @GetMapping("/mentor/{mentorUserId}")
    public ResponseEntity<ApiResponse<List<ReviewDtos.ReviewResponse>>> byMentor(@PathVariable Long mentorUserId) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.byMentor(mentorUserId), "Mentor reviews"));
    }
}
