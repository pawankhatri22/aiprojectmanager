package com.aiprojectmanager.messaging;

import com.aiprojectmanager.session.SessionRepository;
import com.aiprojectmanager.user.CurrentUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repository;
    private final SessionRepository sessionRepository;
    private final CurrentUserService currentUserService;

    public MessageService(MessageRepository repository, SessionRepository sessionRepository, CurrentUserService currentUserService) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.currentUserService = currentUserService;
    }

    public MessageDtos.MessageResponse send(MessageDtos.SendMessageRequest req) {
        var user = currentUserService.get();
        var session = sessionRepository.findById(req.sessionId()).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getMentor().getId().equals(user.getId()) && !session.getGraduate().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not participant");
        }
        Message msg = repository.save(Message.builder().session(session).sender(user).messageText(req.messageText()).timestamp(LocalDateTime.now()).build());
        return toDto(msg);
    }

    public List<MessageDtos.MessageResponse> bySession(Long sessionId) {
        return repository.findBySessionIdOrderByTimestampAsc(sessionId).stream().map(this::toDto).toList();
    }

    private MessageDtos.MessageResponse toDto(Message m) {
        return new MessageDtos.MessageResponse(m.getId(), m.getSession().getId(), m.getSender().getId(), m.getMessageText(), m.getTimestamp());
    }
}
