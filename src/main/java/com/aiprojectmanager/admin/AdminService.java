package com.aiprojectmanager.admin;

import com.aiprojectmanager.payment.PaymentRepository;
import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;

    public AdminService(UserRepository userRepository, SessionRepository sessionRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
    }

    public Page<AdminDtos.UserAdminResponse> users(Pageable p) {
        return userRepository.findAll(p).map(u -> new AdminDtos.UserAdminResponse(u.getId(), u.getEmail(), u.getRole(), u.isEnabled(), u.getCreatedAt()));
    }

    public Page<AdminDtos.SessionAdminResponse> sessions(Pageable p) {
        return sessionRepository.findAll(p).map(s -> new AdminDtos.SessionAdminResponse(s.getId(), s.getMentor().getId(), s.getGraduate().getId(),
                s.getScheduledTime(), s.getDurationMinutes(), s.getPrice(), s.getStatus(), s.getConfirmationLink(), s.getMeetingLink()));
    }

    public Page<AdminDtos.PaymentAdminResponse> payments(Pageable p) {
        return paymentRepository.findAll(p).map(x -> new AdminDtos.PaymentAdminResponse(x.getId(), x.getSession().getId(), x.getAmount(), x.getCurrency(),
                x.getStatus(), x.getTransactionReference(), x.getTimestamp()));
    }

    public AdminDtos.UserAdminResponse disable(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(false);
        var saved = userRepository.save(user);
        return new AdminDtos.UserAdminResponse(saved.getId(), saved.getEmail(), saved.getRole(), saved.isEnabled(), saved.getCreatedAt());
    }
}
