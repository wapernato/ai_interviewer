package org.example.service.impl;

import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.EmailVerificationToken;
import org.example.model.User;
import org.example.repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    private EmailVerificationServiceImpl emailVerificationService;

    @BeforeEach
    void setUp() {
        emailVerificationService = new EmailVerificationServiceImpl(emailVerificationTokenRepository);
    }

    @Test
    void createVerificationToken_UserIsNull_shouldThrowNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> emailVerificationService.createVerificationToken(null)
        );

        verifyNoInteractions(emailVerificationTokenRepository);
    }

    @Test
    void createVerificationToken_DataIsValid_shouldReturnToken() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ximeo@gmail.com");

        String token = emailVerificationService.createVerificationToken(user);
        assertThat(token).isNotNull();

        ArgumentCaptor<EmailVerificationToken> captor = ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(emailVerificationTokenRepository).save(captor.capture());
        EmailVerificationToken savedToken = captor.getValue();
        assertThat(savedToken.getToken()).isEqualTo(token);
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.getUsedAt()).isNull();
    }

    @Test
    void verifyEmail_TokenIsNull_shouldReturnBadRequestException() {
        assertThrows(
                BadRequestException.class,
                () -> emailVerificationService.verifyEmail(null)
        );

        verifyNoInteractions(emailVerificationTokenRepository);
    }

    @Test
    void verifyEmail_TokenNotFound_shouldReturnNotFoundException() {
        String token = "token-missing";
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> emailVerificationService.verifyEmail(token));

        verify(emailVerificationTokenRepository).findByToken(token);
    }

    @Test
    void verifyEmail_TokenUsedAt_shouldReturnBadRequestException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("rodion@gmail.com");

        String token = "token-active";

        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        verificationToken.setUsedAt(Instant.now());

        assertThrows(BadRequestException.class,
                () -> emailVerificationService.verifyEmail(token));

        verify(emailVerificationTokenRepository).findByToken(token);
        assertThat(user.getEnabled()).isFalse();
    }

    @Test
    void verifyEmail_TokenHasExpired_shouldReturnBadRequestException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("artem@mail.ru");

        String token = "active-token";

        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                Instant.now().minus(15, ChronoUnit.MINUTES)
        );

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        assertThrows(BadRequestException.class,
                () -> emailVerificationService.verifyEmail(token));

        verify(emailVerificationTokenRepository).findByToken(token);
        assertThat(user.getEnabled()).isFalse();
    }

    @Test
    void verifyEmail_DataIsValid_shouldReturnVoid() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ximeo@gmail.com");

        String token = "active-token";

        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        emailVerificationService.verifyEmail(token);

        assertThat(user.getEnabled()).isTrue();
        assertThat(verificationToken.getUsedAt()).isNotNull();
    }


}
