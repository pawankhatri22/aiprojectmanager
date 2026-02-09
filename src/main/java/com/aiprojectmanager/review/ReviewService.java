package com.aiprojectmanager.review;

import com.aiprojectmanager.profile.MentorProfileRepository;
import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.session.SessionStatus;
import com.aiprojectmanager.user.CurrentUserService;
import com.aiprojectmanager.user.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;
    private final MentorProfileRepository mentorProfileRepository;

    public ReviewService(ReviewRepository reviewRepository, SessionRepository sessionRepository, CurrentUserService currentUserService, MentorProfileRepository mentorProfileRepository) {
        this.reviewRepository = reviewRepository;
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
        this.mentorProfileRepository = mentorProfileRepository;
    }

    public ReviewDtos.ReviewResponse create(ReviewDtos.CreateReviewRequest req) {
        var graduate = currentUserService.get();
        if (graduate.getRole() != Role.GRADUATE) throw new IllegalArgumentException("Graduate role required");

        var session = sessionRepository.findById(req.sessionId()).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getGraduate().getId().equals(graduate.getId())) throw new IllegalArgumentException("Only owning graduate can review");
        if (reviewRepository.findBySessionId(req.sessionId()).isPresent()) throw new IllegalArgumentException("Review already submitted for this session");

        LocalDateTime sessionEnd = session.getScheduledTime().plusMinutes(session.getDurationMinutes());
        boolean endedByTime = LocalDateTime.now().isAfter(sessionEnd);
        boolean endedByStatus = session.getStatus() == SessionStatus.COMPLETED;
        if (!endedByTime && !endedByStatus) throw new IllegalArgumentException("Review allowed only after session completion");

        var review = reviewRepository.save(Review.builder()
                .session(session)
                .mentor(session.getMentor())
                .graduate(graduate)
                .rating(req.rating())
                .comment(req.comment())
                .createdAt(LocalDateTime.now())
                .build());

        recalculateMentorRating(session.getMentor().getId());
        return toDto(review);
    }

    public List<ReviewDtos.ReviewResponse> byMentor(Long mentorUserId) {
        return reviewRepository.findByMentorIdOrderByCreatedAtDesc(mentorUserId).stream().map(this::toDto).toList();
    }

    private void recalculateMentorRating(Long mentorUserId) {
        var profile = mentorProfileRepository.findByUserId(mentorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor profile missing"));
        var reviews = reviewRepository.findByMentorId(mentorUserId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        profile.setRatingAverage(Math.round(avg * 100.0) / 100.0);
        mentorProfileRepository.save(profile);
    }

    private ReviewDtos.ReviewResponse toDto(Review r) {
        return new ReviewDtos.ReviewResponse(r.getId(), r.getSession().getId(), r.getMentor().getId(), r.getGraduate().getId(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
