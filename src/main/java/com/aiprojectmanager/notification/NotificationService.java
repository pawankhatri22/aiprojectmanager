package com.aiprojectmanager.notification;

import com.aiprojectmanager.email.EmailGateway;
import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final CurrentUserService currentUserService;
    private final EmailGateway emailGateway;

    public NotificationService(NotificationRepository repository, CurrentUserService currentUserService, EmailGateway emailGateway) {
        this.repository = repository;
        this.currentUserService = currentUserService;
        this.emailGateway = emailGateway;
    }

    public void notify(User user, NotificationType type, String message) {
        repository.save(UserNotification.builder()
                .user(user)
                .type(type)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
        emailGateway.send(user.getEmail(), "AI Project Manager - " + type.name(), message);
    }

    public List<NotificationDtos.NotificationResponse> myNotifications() {
        var user = currentUserService.get();
        return repository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(n -> new NotificationDtos.NotificationResponse(n.getId(), n.getType(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    public void markRead(Long id) {
        var user = currentUserService.get();
        var n = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getUser().getId().equals(user.getId())) throw new IllegalArgumentException("Not your notification");
        n.setRead(true);
        repository.save(n);
    }
}
