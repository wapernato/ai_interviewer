package org.example.service.impl;

import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.exception.BadRequestException;
import org.example.exception.UserAlreadyExistsException;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.example.security.JwtService;
import org.example.security.LoginRateLimiter;
import org.example.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginRateLimiter loginRateLimiter;

    public AuthServiceImpl(JwtService jwtService,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           LoginRateLimiter loginRateLimiter) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginRateLimiter = loginRateLimiter;
    }

    private String normalizedEmail(String email) {

        if(email == null){
            throw new BadRequestException("Почта не может быть null.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if(normalizedEmail.isBlank()){
            throw new BadRequestException("Почта не может быть пустой.");
        }

        return normalizedEmail;
    }

    private AuthResponse createResponse(User user) {
        AuthResponse response = new AuthResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }

    private AuthResponse createResponseWithToken(User user) {
        AuthResponse response = new AuthResponse();

        response.setToken(jwtService.generateToken(user));
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername();
        if(username == null){
            throw new BadRequestException("Имя пользователя не может быть null.");
        }
        String trimUsername = username.trim();

        if(trimUsername.isBlank()){
            throw new BadRequestException("Имя пользователя не может быть пустым.");
        }

        String normalizedEmail = normalizedEmail(request.getEmail());

        if (userRepository.existsByUsername(trimUsername)) {
            throw new UserAlreadyExistsException("Пользователь с таким username уже существует.");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует.");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setUsername(trimUsername);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        User userSaved = userRepository.save(user);

        return createResponse(userSaved);
    }

    @Override
    public AuthResponse login(LoginRequest request, String clientIp) {
        String normalizedEmail = normalizedEmail(request.getEmail());
        loginRateLimiter.checkLimit(clientIp, normalizedEmail);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    loginRateLimiter.registerFailedAttempt(clientIp, normalizedEmail);
                    return new BadRequestException("Неверный email или пароль.");
                });

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if(!passwordMatches){
            loginRateLimiter.registerFailedAttempt(clientIp, normalizedEmail);
            throw new BadRequestException("Неверный email или пароль.");
        }

        loginRateLimiter.resetAttempts(clientIp, normalizedEmail);
        return createResponseWithToken(user);
    }
}
