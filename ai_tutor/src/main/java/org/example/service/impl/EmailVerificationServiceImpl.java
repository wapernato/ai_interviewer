package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.EmailVerificationToken;
import org.example.model.User;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.service.EmailVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    public EmailVerificationServiceImpl(EmailVerificationTokenRepository emailVerificationTokenRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
    }

    @Transactional
    @Override
    public String createVerificationToken(User user) {
        if(user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }

        Instant now = Instant.now();

        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                now.plus(15, ChronoUnit.MINUTES)
        );

        emailVerificationTokenRepository.save(verificationToken);

        return token;
    }

    @Transactional
    @Override
    public void verifyEmail(String token) {
        if(token == null || token.isBlank()) {
            throw new BadRequestException("Токен передан некорректно.");
        }

        EmailVerificationToken verifyToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Токен подтверждения email не найден."));

        if(verifyToken.getUsedAt() != null) {
            throw new BadRequestException("Токен уже недействителен.");
        }

        if(verifyToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Срок действия токена истек.");
        }

        User user = verifyToken.getUser();
        user.setEnabled(true);

        verifyToken.setUsedAt(Instant.now());
    }
}
