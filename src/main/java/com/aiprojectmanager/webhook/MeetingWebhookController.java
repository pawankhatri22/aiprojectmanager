package com.aiprojectmanager.webhook;

import com.aiprojectmanager.attendance.AttendanceService;
import com.aiprojectmanager.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class MeetingWebhookController {
    private final AttendanceService attendanceService;

    public MeetingWebhookController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/meeting/attendance")
    public ResponseEntity<ApiResponse<Void>> attendance(@RequestBody Map<String, String> body) {
        Long sessionId = Long.valueOf(body.get("sessionId"));
        String participantEmail = body.get("participantEmail");
        String eventType = body.get("eventType"); // join|leave
        LocalDateTime ts = body.get("timestamp") == null ? LocalDateTime.now() : LocalDateTime.parse(body.get("timestamp"));

        if ("join".equalsIgnoreCase(eventType)) {
            attendanceService.recordJoin(sessionId, participantEmail, ts);
        } else if ("leave".equalsIgnoreCase(eventType)) {
            attendanceService.recordLeave(sessionId, participantEmail, ts);
        }
        return ResponseEntity.ok(ApiResponse.ok(null, "Webhook processed"));
    }
}
