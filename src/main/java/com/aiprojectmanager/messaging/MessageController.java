package com.aiprojectmanager.messaging;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) { this.service = service; }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<MessageDtos.MessageResponse>> send(@Valid @RequestBody MessageDtos.SendMessageRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.send(req), "Message sent"));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<List<MessageDtos.MessageResponse>>> bySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ApiResponse.ok(service.bySession(sessionId), "Messages"));
    }
}
