package com.aiprojectmanager.reschedule;

import com.aiprojectmanager.notification.NotificationService;
import com.aiprojectmanager.notification.NotificationType;
import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.session.SessionService;
import com.aiprojectmanager.user.CurrentUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RescheduleService {
    private final RescheduleRequestRepository repository;
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;
    private final SessionService sessionService;
    private final NotificationService notificationService;

    public RescheduleService(RescheduleRequestRepository repository, SessionRepository sessionRepository, CurrentUserService currentUserService,
                             SessionService sessionService, NotificationService notificationService) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
        this.sessionService = sessionService;
        this.notificationService = notificationService;
    }

    public RescheduleDtos.RescheduleResponse request(Long sessionId, RescheduleDtos.CreateRescheduleRequest req) {
        var user = currentUserService.get();
        var session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        boolean participant = session.getGraduate().getId().equals(user.getId()) || session.getMentor().getId().equals(user.getId());
        if (!participant) throw new IllegalArgumentException("Only session participants can request reschedule");

        var rr = repository.save(RescheduleRequest.builder()
                .session(session)
                .requestedBy(user)
                .oldScheduledTime(session.getScheduledTime())
                .oldDurationMinutes(session.getDurationMinutes())
                .newScheduledTime(req.newScheduledTime())
                .newDurationMinutes(req.newDurationMinutes())
                .reason(req.reason())
                .status(RescheduleStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .build());

        var target = session.getGraduate().getId().equals(user.getId()) ? session.getMentor() : session.getGraduate();
        notificationService.notify(target, NotificationType.RESCHEDULE_REQUESTED,
                "Reschedule requested for session #" + session.getId() + " to " + req.newScheduledTime());
        return toDto(rr);
    }

    public RescheduleDtos.RescheduleResponse decide(Long requestId, boolean accept) {
        var user = currentUserService.get();
        var rr = repository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Reschedule request not found"));
        var session = rr.getSession();
        boolean participant = session.getGraduate().getId().equals(user.getId()) || session.getMentor().getId().equals(user.getId());
        if (!participant) throw new IllegalArgumentException("Only session participants can decide");
        if (rr.getStatus() != RescheduleStatus.REQUESTED) throw new IllegalArgumentException("Already decided");

        if (accept) {
            sessionService.validateMentorAvailability(session.getMentor().getId(), rr.getNewScheduledTime(), rr.getNewDurationMinutes(), session.getId());
            session.setScheduledTime(rr.getNewScheduledTime());
            session.setDurationMinutes(rr.getNewDurationMinutes());
            sessionRepository.save(session);
            rr.setStatus(RescheduleStatus.ACCEPTED);
            notificationService.notify(session.getGraduate(), NotificationType.RESCHEDULE_ACCEPTED,
                    "Reschedule accepted for session #" + session.getId());
            notificationService.notify(session.getMentor(), NotificationType.RESCHEDULE_ACCEPTED,
                    "Reschedule accepted for session #" + session.getId());
        } else {
            rr.setStatus(RescheduleStatus.REJECTED);
            notificationService.notify(session.getGraduate(), NotificationType.RESCHEDULE_REJECTED,
                    "Reschedule rejected for session #" + session.getId());
            notificationService.notify(session.getMentor(), NotificationType.RESCHEDULE_REJECTED,
                    "Reschedule rejected for session #" + session.getId());
        }

        rr.setDecidedAt(LocalDateTime.now());
        return toDto(repository.save(rr));
    }

    public List<RescheduleDtos.RescheduleResponse> bySession(Long sessionId) {
        return repository.findBySessionIdOrderByCreatedAtDesc(sessionId).stream().map(this::toDto).toList();
    }

    private RescheduleDtos.RescheduleResponse toDto(RescheduleRequest x) {
        return new RescheduleDtos.RescheduleResponse(x.getId(), x.getSession().getId(), x.getRequestedBy().getId(), x.getOldScheduledTime(), x.getOldDurationMinutes(),
                x.getNewScheduledTime(), x.getNewDurationMinutes(), x.getReason(), x.getStatus(), x.getCreatedAt(), x.getDecidedAt());
    }
}
