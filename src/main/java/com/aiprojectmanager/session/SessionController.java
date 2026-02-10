package com.aiprojectmanager.session;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SessionController {
    private final SessionService service;

    public SessionController(SessionService service) { this.service = service; }

    @PostMapping("/sessions/request")
    public ResponseEntity<ApiResponse<SessionDtos.SessionResponse>> request(@Valid @RequestBody SessionDtos.RequestSessionRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.request(req), "Session requested"));
    }

    @GetMapping("/sessions/my")
    public ResponseEntity<ApiResponse<Page<SessionDtos.SessionResponse>>> my(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.my(PageRequest.of(page, size)), "My sessions"));
    }

    @PutMapping("/sessions/{id}/cancel")
    public ResponseEntity<ApiResponse<SessionDtos.SessionResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.cancel(id), "Session cancelled"));
    }

    @PutMapping("/sessions/{id}/complete")
    public ResponseEntity<ApiResponse<SessionDtos.SessionResponse>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.complete(id), "Session completed"));
    }

    @GetMapping("/mentor/sessions")
    public ResponseEntity<ApiResponse<Page<SessionDtos.SessionResponse>>> mentorSessions(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.my(PageRequest.of(page, size)), "Mentor sessions"));
    }

    @PutMapping("/mentor/session/{id}/approve")
    public ResponseEntity<ApiResponse<SessionDtos.SessionResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.approve(id), "Session approved"));
    }

    @PutMapping("/mentor/session/{id}/reject")
    public ResponseEntity<ApiResponse<SessionDtos.SessionResponse>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.reject(id), "Session rejected"));
    }
}
