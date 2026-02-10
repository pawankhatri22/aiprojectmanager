package com.aiprojectmanager.admin;

import com.aiprojectmanager.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) { this.service = service; }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminDtos.UserAdminResponse>>> users(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.users(PageRequest.of(page, size)), "Users"));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<ApiResponse<AdminDtos.UserAdminResponse>> disable(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.disable(id), "User disabled"));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<AdminDtos.SessionAdminResponse>>> sessions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.sessions(PageRequest.of(page, size)), "Sessions"));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Page<AdminDtos.PaymentAdminResponse>>> payments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.payments(PageRequest.of(page, size)), "Payments"));
    }
}
