package com.aiprojectmanager.notification;

import com.aiprojectmanager.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NotificationDtos.NotificationResponse>>> my() {
        return ResponseEntity.ok(ApiResponse.ok(service.myNotifications(), "Notifications"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id) {
        service.markRead(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Marked as read"));
    }
}
