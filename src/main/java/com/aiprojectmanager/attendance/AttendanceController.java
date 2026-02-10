package com.aiprojectmanager.attendance;

import com.aiprojectmanager.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<AttendanceDtos.AttendanceResponse>>> bySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.bySession(sessionId), "Session attendance"));
    }
}
