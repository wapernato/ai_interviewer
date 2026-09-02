package org.example.service;

public interface EmailSenderService {
    void sendEmailVerification(String to, String verificationLink);
}
