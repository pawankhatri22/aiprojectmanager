package com.aiprojectmanager.attendance;

import com.aiprojectmanager.notification.NotificationService;
import com.aiprojectmanager.notification.NotificationType;
import com.aiprojectmanager.session.SessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {
    private final SessionAttendanceRepository repository;
    private final SessionRepository sessionRepository;
    private final NotificationService notificationService;

    public AttendanceService(SessionAttendanceRepository repository, SessionRepository sessionRepository, NotificationService notificationService) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.notificationService = notificationService;
    }

    public void recordJoin(Long sessionId, String participantEmail, LocalDateTime when) {
        var session = sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        var user = session.getMentor().getEmail().equalsIgnoreCase(participantEmail) ? session.getMentor() : session.getGraduate();
        var rec = repository.findBySessionIdAndParticipantEmail(sessionId, participantEmail).orElse(SessionAttendance.builder()
                .session(session)
                .participant(user)
                .build());
        rec.setJoinTime(when);
        rec.setStatus(when.isAfter(session.getScheduledTime().plusMinutes(10)) ? AttendanceStatus.LATE : AttendanceStatus.ON_TIME);
        rec.setUpdatedAt(LocalDateTime.now());
        repository.save(rec);
    }

    public void recordLeave(Long sessionId, String participantEmail, LocalDateTime when) {
        var rec = repository.findBySessionIdAndParticipantEmail(sessionId, participantEmail)
                .orElseThrow(() -> new IllegalArgumentException("Join record not found"));
        rec.setLeaveTime(when);
        if (rec.getJoinTime() != null) {
            var actual = java.time.Duration.between(rec.getJoinTime(), when).toMinutes();
            if (actual < rec.getSession().getDurationMinutes() / 2) rec.setStatus(AttendanceStatus.LEFT_EARLY);
        }
        rec.setUpdatedAt(LocalDateTime.now());
        repository.save(rec);
    }

    public List<AttendanceDtos.AttendanceResponse> bySession(Long sessionId) {
        return repository.findBySessionId(sessionId).stream().map(a ->
                new AttendanceDtos.AttendanceResponse(a.getId(), a.getSession().getId(), a.getParticipant().getId(), a.getParticipant().getEmail(),
                        a.getJoinTime(), a.getLeaveTime(), a.getStatus(), a.getUpdatedAt())
        ).toList();
    }

    @Scheduled(fixedDelay = 300000)
    public void meetingStartingSoonReminder() {
        var now = LocalDateTime.now();
        var soon = now.plusMinutes(30);
        var sessions = sessionRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("PAID"))
                .filter(s -> s.getScheduledTime().isAfter(now) && s.getScheduledTime().isBefore(soon))
                .toList();
        sessions.forEach(s -> {
            notificationService.notify(s.getGraduate(), NotificationType.MEETING_SOON,
                    "Meeting starts soon for session #" + s.getId() + " at " + s.getScheduledTime());
            notificationService.notify(s.getMentor(), NotificationType.MEETING_SOON,
                    "Meeting starts soon for session #" + s.getId() + " at " + s.getScheduledTime());
        });
    }

    @Scheduled(fixedDelay = 600000)
    public void markNoShow() {
        var now = LocalDateTime.now();
        var sessions = sessionRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("PAID"))
                .filter(s -> s.getScheduledTime().plusMinutes(15).isBefore(now))
                .toList();
        for (var s : sessions) {
            for (var participant : List.of(s.getGraduate(), s.getMentor())) {
                repository.findBySessionIdAndParticipantEmail(s.getId(), participant.getEmail()).orElseGet(() ->
                        repository.save(SessionAttendance.builder()
                                .session(s)
                                .participant(participant)
                                .status(AttendanceStatus.NO_SHOW)
                                .updatedAt(LocalDateTime.now())
                                .build())
                );
            }
        }
    }
}
