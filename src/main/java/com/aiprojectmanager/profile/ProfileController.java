package com.aiprojectmanager.profile;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) { this.service = service; }

    @PostMapping("/mentor/profile")
    public ResponseEntity<ApiResponse<ProfileDtos.MentorProfileResponse>> createMentor(@Valid @RequestBody ProfileDtos.MentorProfileRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.upsertMentor(req), "Mentor profile saved"));
    }

    @PutMapping("/mentor/profile")
    public ResponseEntity<ApiResponse<ProfileDtos.MentorProfileResponse>> updateMentor(@Valid @RequestBody ProfileDtos.MentorProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.upsertMentor(req), "Mentor profile updated"));
    }

    @GetMapping("/graduate/profile")
    public ResponseEntity<ApiResponse<ProfileDtos.GraduateProfileResponse>> getGrad() {
        return ResponseEntity.ok(ApiResponse.ok(service.myGraduateProfile(), "Graduate profile"));
    }

    @PutMapping("/graduate/profile")
    public ResponseEntity<ApiResponse<ProfileDtos.GraduateProfileResponse>> upsertGrad(@Valid @RequestBody ProfileDtos.GraduateProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.upsertGraduate(req), "Graduate profile updated"));
    }
}
