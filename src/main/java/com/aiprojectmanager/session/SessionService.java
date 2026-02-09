package com.aiprojectmanager.session;

import com.aiprojectmanager.profile.MentorProfileRepository;
import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.Role;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        var mentorProfile = mentorProfileRepository.findByUserId(mentor.getId()).orElseThrow(() -> new IllegalArgumentException("Mentor profile missing"));
        BigDecimal price = mentorProfile.getHourlyRate().multiply(BigDecimal.valueOf(req.durationMinutes() / 60.0));
        Session s = sessionRepository.save(Session.builder().mentor(mentor).graduate(graduate).scheduledTime(req.scheduledTime())
                .durationMinutes(req.durationMinutes()).price(price).status(SessionStatus.REQUESTED).createdAt(LocalDateTime.now()).build());
        return toDto(s);
    }

    public Page<SessionDtos.SessionResponse> my(Pageable pageable) {
        var u = currentUserService.get();
        return sessionRepository.findByMentorIdOrGraduateId(u.getId(), u.getId(), pageable).map(this::toDto);
    }

    public SessionDtos.SessionResponse cancel(Long id) { return updateStatus(id, SessionStatus.CANCELLED, null); }
    public SessionDtos.SessionResponse approve(Long id) { return updateStatus(id, SessionStatus.APPROVED, Role.MENTOR); }
    public SessionDtos.SessionResponse reject(Long id) { return updateStatus(id, SessionStatus.REJECTED, Role.MENTOR); }

    private SessionDtos.SessionResponse updateStatus(Long id, SessionStatus status, Role requiredRole) {
        var user = currentUserService.get();
        if (requiredRole != null && user.getRole() != requiredRole) throw new IllegalArgumentException("Insufficient role");
        Session s = sessionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (requiredRole == Role.MENTOR && !s.getMentor().getId().equals(user.getId())) throw new IllegalArgumentException("Not your session");
        if (status == SessionStatus.CANCELLED && !s.getGraduate().getId().equals(user.getId())) throw new IllegalArgumentException("Only graduate can cancel");
        s.setStatus(status);
        return toDto(sessionRepository.save(s));
    }

    private SessionDtos.SessionResponse toDto(Session s) {
        return new SessionDtos.SessionResponse(s.getId(), s.getMentor().getId(), s.getGraduate().getId(), s.getScheduledTime(), s.getDurationMinutes(), s.getPrice(), s.getStatus());
    }
}
