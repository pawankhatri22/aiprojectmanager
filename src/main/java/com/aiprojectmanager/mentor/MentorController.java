package com.aiprojectmanager.mentor;

import com.aiprojectmanager.common.ApiResponse;
import com.aiprojectmanager.profile.ProfileDtos;
import com.aiprojectmanager.profile.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {
    private final MentorService mentorService;
    private final ProfileService profileService;

    public MentorController(MentorService mentorService, ProfileService profileService) {
        this.mentorService = mentorService;
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProfileDtos.MentorProfileResponse>>> list(@RequestParam(defaultValue = "") String search,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                      @RequestParam(defaultValue = "") String sortBy) {
        Sort sort = "topRated".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Order.desc("ratingAverage"), Sort.Order.asc("fullName"))
                : Sort.by(Sort.Order.asc("fullName"));
        return ResponseEntity.ok(ApiResponse.ok(mentorService.list(search, PageRequest.of(page, size, sort)), "Mentors"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDtos.MentorProfileResponse>> byId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(profileService.mentorById(id), "Mentor details"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ProfileDtos.MentorProfileResponse>> byUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(profileService.mentorByUserId(userId), "Mentor details"));
    }
}
