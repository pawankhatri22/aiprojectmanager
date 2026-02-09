package com.aiprojectmanager.payment;

import com.aiprojectmanager.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping("/pay/{sessionId}")
    public ResponseEntity<ApiResponse<PaymentDtos.PaymentResponse>> pay(@PathVariable Long sessionId) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.pay(sessionId), "Payment completed"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PaymentDtos.PaymentResponse>>> my(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.my(PageRequest.of(page, size)), "My payments"));
    }
}
