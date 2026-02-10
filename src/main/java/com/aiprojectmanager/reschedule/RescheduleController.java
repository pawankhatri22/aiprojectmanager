package com.aiprojectmanager.reschedule;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RescheduleController {
    private final RescheduleService service;

    public RescheduleController(RescheduleService service) {
        this.service = service;
    }

    @PostMapping("/sessions/{sessionId}/reschedule-request")
    public ResponseEntity<ApiResponse<RescheduleDtos.RescheduleResponse>> request(@PathVariable Long sessionId,
                                                                                   @Valid @RequestBody RescheduleDtos.CreateRescheduleRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.request(sessionId, req), "Reschedule request created"));
    }

    @GetMapping("/sessions/{sessionId}/reschedule-requests")
    public ResponseEntity<ApiResponse<List<RescheduleDtos.RescheduleResponse>>> bySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.bySession(sessionId), "Reschedule audit trail"));
    }

    @PutMapping("/reschedule/{requestId}/accept")
    public ResponseEntity<ApiResponse<RescheduleDtos.RescheduleResponse>> accept(@PathVariable Long requestId) {
        return ResponseEntity.ok(ApiResponse.ok(service.decide(requestId, true), "Reschedule accepted"));
    }

    @PutMapping("/reschedule/{requestId}/reject")
    public ResponseEntity<ApiResponse<RescheduleDtos.RescheduleResponse>> reject(@PathVariable Long requestId) {
        return ResponseEntity.ok(ApiResponse.ok(service.decide(requestId, false), "Reschedule rejected"));
    }
}
