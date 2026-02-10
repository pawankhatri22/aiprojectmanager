package com.aiprojectmanager.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingEmailGateway implements EmailGateway {
    private static final Logger log = LoggerFactory.getLogger(LoggingEmailGateway.class);

    @Override
    public void send(String toEmail, String subject, String body) {
        log.info("EMAIL to={} subject={} body={}", toEmail, subject, body);
    }
}
