package com.aiprojectmanager.session;

import com.aiprojectmanager.profile.MentorProfileRepository;
import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.Role;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;

    public SessionService(SessionRepository sessionRepository, CurrentUserService currentUserService, UserRepository userRepository, MentorProfileRepository mentorProfileRepository) {
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.mentorProfileRepository = mentorProfileRepository;
    }

    public SessionDtos.SessionResponse request(SessionDtos.RequestSessionRequest req) {
        var graduate = currentUserService.get();
        if (graduate.getRole() != Role.GRADUATE) throw new IllegalArgumentException("Graduate role required");
        var mentor = userRepository.findById(req.mentorId()).orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        if (mentor.getRole() != Role.MENTOR) throw new IllegalArgumentException("Selected user is not a mentor");
        var mentorProfile = mentorProfileRepository.findByUserId(mentor.getId()).orElseThrow(() -> new IllegalArgumentException("Mentor profile missing"));

        ensureNoPaidConflict(mentor.getId(), req.scheduledTime(), req.durationMinutes(), null);

        BigDecimal price = mentorProfile.getHourlyRate()
                .multiply(BigDecimal.valueOf(req.durationMinutes()))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Session s = sessionRepository.save(Session.builder()
                .mentor(mentor)
                .graduate(graduate)
                .scheduledTime(req.scheduledTime())
                .durationMinutes(req.durationMinutes())
                .price(price)
                .status(SessionStatus.REQUESTED)
                .createdAt(LocalDateTime.now())
                .build());
        return toDto(s);
    }

    public Page<SessionDtos.SessionResponse> my(Pageable pageable) {
        var u = currentUserService.get();
        return sessionRepository.findByMentorIdOrGraduateId(u.getId(), u.getId(), pageable).map(this::toDto);
    }

    public SessionDtos.SessionResponse cancel(Long id) { return updateStatus(id, SessionStatus.CANCELLED, null); }
    public SessionDtos.SessionResponse approve(Long id) { return updateStatus(id, SessionStatus.PENDING_PAYMENT, Role.MENTOR); }
    public SessionDtos.SessionResponse reject(Long id) { return updateStatus(id, SessionStatus.REJECTED, Role.MENTOR); }
    public SessionDtos.SessionResponse markPaid(Long id) { return updateStatus(id, SessionStatus.PAID, Role.GRADUATE); }
    public SessionDtos.SessionResponse complete(Long id) { return updateStatus(id, SessionStatus.COMPLETED, Role.GRADUATE); }

    private SessionDtos.SessionResponse updateStatus(Long id, SessionStatus status, Role requiredRole) {
        var user = currentUserService.get();
        if (requiredRole != null && user.getRole() != requiredRole) throw new IllegalArgumentException("Insufficient role");
        Session s = sessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (requiredRole == Role.MENTOR && !s.getMentor().getId().equals(user.getId())) throw new IllegalArgumentException("Not your session");
        if (status == SessionStatus.CANCELLED && !s.getGraduate().getId().equals(user.getId())) throw new IllegalArgumentException("Only graduate can cancel");
        if (status == SessionStatus.PAID && !s.getGraduate().getId().equals(user.getId())) throw new IllegalArgumentException("Only graduate can complete payment");
        if (status == SessionStatus.COMPLETED && !s.getGraduate().getId().equals(user.getId())) throw new IllegalArgumentException("Only graduate can mark complete");

        if (status == SessionStatus.PENDING_PAYMENT) {
            if (s.getStatus() != SessionStatus.REQUESTED) throw new IllegalArgumentException("Only requested sessions can be approved");
            s.setConfirmationLink("https://confirm.aiprojectmanager.local/session/" + s.getId() + "?token=" + UUID.randomUUID());
        }

        if (status == SessionStatus.PAID) {
            if (s.getStatus() != SessionStatus.PENDING_PAYMENT) throw new IllegalArgumentException("Session must be pending payment");
            ensureNoPaidConflict(s.getMentor().getId(), s.getScheduledTime(), s.getDurationMinutes(), s.getId());
            s.setMeetingLink("https://meet.aiprojectmanager.local/room/" + UUID.randomUUID());
        }

        if (status == SessionStatus.COMPLETED) {
            if (s.getStatus() != SessionStatus.PAID) throw new IllegalArgumentException("Only paid session can be marked complete");
        }

        s.setStatus(status);
        return toDto(sessionRepository.save(s));
    }

    private void ensureNoPaidConflict(Long mentorId, LocalDateTime start, Integer duration, Long ignoreSessionId) {
        LocalDateTime end = start.plusMinutes(duration);
        List<Session> paidSessions = sessionRepository.findByMentorIdAndStatusInAndScheduledTimeBetween(
                mentorId,
                List.of(SessionStatus.PAID),
                start.minusHours(12),
                end.plusHours(12)
        );

        boolean conflict = paidSessions.stream()
                .filter(s -> ignoreSessionId == null || !s.getId().equals(ignoreSessionId))
                .anyMatch(s -> overlaps(start, end, s.getScheduledTime(), s.getScheduledTime().plusMinutes(s.getDurationMinutes())));

        if (conflict) throw new IllegalArgumentException("Mentor already has a paid session in that time slot");
    }

    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && aEnd.isAfter(bStart);
    }

    public SessionDtos.SessionResponse toDto(Session s) {
        return new SessionDtos.SessionResponse(
                s.getId(), s.getMentor().getId(), s.getGraduate().getId(), s.getScheduledTime(),
                s.getDurationMinutes(), s.getPrice(), s.getStatus(), s.getConfirmationLink(), s.getMeetingLink()
        );
    }
}
