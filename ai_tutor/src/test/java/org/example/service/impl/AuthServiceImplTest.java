package org.example.service.impl;

import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.example.security.JwtService;
import org.example.security.LoginRateLimiter;
import org.example.security.PasswordPolicyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private static final String VALID_PASSWORD = "StrongPass1!";

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LoginRateLimiter loginRateLimiter;
    @Mock
    private PasswordPolicyValidator passwordPolicyValidator;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp(){
        authService = new AuthServiceImpl(
                jwtService,
                userRepository,
                passwordEncoder,
                loginRateLimiter,
                passwordPolicyValidator
        );
    }

    private RegisterRequest createRequest(String email, String username, String password){
        RegisterRequest request = new RegisterRequest();

        request.setEmail(email);
        request.setPassword(password);
        request.setUsername(username);

        return request;
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid(){
        RegisterRequest request = createRequest("zavod3433@yandex.ru", "ximeo", VALID_PASSWORD);
        when(userRepository.existsByUsername("ximeo")).thenReturn(false);
        when(userRepository.existsByEmail("zavod3433@yandex.ru")).thenReturn(false);
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn("encoded-password");

            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

        AuthResponse response = authService.register(request);


        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("ximeo");
        assertThat(response.getEmail()).isEqualTo("zavod3433@yandex.ru");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getToken()).isNull();

        verify(passwordPolicyValidator).validate(VALID_PASSWORD);
        verify(userRepository).save(argThat(user ->
                "ximeo".equals(user.getUsername())
                        && "zavod3433@yandex.ru".equals(user.getEmail())
                        && "encoded-password".equals(user.getPasswordHash())
                        && user.getRole() == UserRole.USER
                        && Boolean.TRUE.equals(user.getEnabled())
        ));
    }

    @Test
    void register_shouldNormalizeEmailAndTrimUsername_whenRequestIsValid() {
        String username = "  ximeo  ";
        String email = " Zavod3433@yandex.ru  ";
        RegisterRequest request = createRequest(email, username, VALID_PASSWORD);

        String trimUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase();

        when(userRepository.existsByUsername(trimUsername)).thenReturn(false);
        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(false);
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn("encoded-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
           User user = invocation.getArgument(0);
           user.setId(1L);
           return user;
        });

        AuthResponse response = authService.register(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("ximeo");
        assertThat(response.getEmail()).isEqualTo("zavod3433@yandex.ru");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getToken()).isNull();

        verify(passwordPolicyValidator).validate(VALID_PASSWORD);
        verify(userRepository).save(argThat(user ->
                "ximeo".equals(user.getUsername())
                        && "zavod3433@yandex.ru".equals(user.getEmail())
                        && "encoded-password".equals(user.getPasswordHash())
                        && user.getRole() == UserRole.USER
                        && Boolean.TRUE.equals(user.getEnabled())
        ));
    }
}
