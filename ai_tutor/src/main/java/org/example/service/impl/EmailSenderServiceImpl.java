package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.service.EmailSenderService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;

    public EmailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmailVerification(String to, String verificationLink) {
        if (to == null || to.isBlank()) {
            throw new BadRequestException("Email получателя не должен быть пустым.");
        }

        if (verificationLink == null || verificationLink.isBlank()) {
            throw new BadRequestException("Ссылка подтверждения не должна быть пустой.");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Подтверждение email");
        message.setText("Для подтверждения email перейдите по ссылке: " + verificationLink);

        javaMailSender.send(message);
    }
}
