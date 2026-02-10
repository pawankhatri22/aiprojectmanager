package com.aiprojectmanager.email;

public interface EmailGateway {
    void send(String toEmail, String subject, String body);
}
