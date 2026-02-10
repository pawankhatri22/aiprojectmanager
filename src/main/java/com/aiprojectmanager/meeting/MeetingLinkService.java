package com.aiprojectmanager.meeting;

import com.aiprojectmanager.session.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Service
public class MeetingLinkService {
    private final RestTemplate restTemplate;
    private final String googleApiKey;
    private final String googleCalendarId;
    private final String googleOAuthToken;

    public MeetingLinkService(RestTemplateBuilder restTemplateBuilder,
                              @Value("${app.meeting.google.api-key:}") String googleApiKey,
                              @Value("${app.meeting.google.calendar-id:primary}") String googleCalendarId,
                              @Value("${app.meeting.google.oauth-token:}") String googleOAuthToken) {
        this.restTemplate = restTemplateBuilder.build();
        this.googleApiKey = googleApiKey;
        this.googleCalendarId = googleCalendarId;
        this.googleOAuthToken = googleOAuthToken;
    }

    public String createMeetingLink(Session session) {

        try {

        } catch (Exception ignored) {
        }

        return fallbackLink();
    }

    private String fallbackLink() {
        return "https://meet.aiprojectmanager.local/room/" + UUID.randomUUID();
    }
}
