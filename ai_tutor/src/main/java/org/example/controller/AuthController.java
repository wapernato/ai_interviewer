package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.PasswordStrengthRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.auth.AuthResponse;
import org.example.security.ClientIpResolver;
import org.example.security.PasswordStrengthEvaluator;
import org.example.security.PasswordStrengthResult;
import org.example.service.AuthService;
import org.example.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ClientIpResolver clientIpResolver;
    private final PasswordStrengthEvaluator passwordStrengthEvaluator;
    private final EmailVerificationService emailVerificationService;

    public AuthController(AuthService authService,
                          ClientIpResolver clientIpResolver,
                          PasswordStrengthEvaluator passwordStrengthEvaluator,
                          EmailVerificationService emailVerificationService){
        this.authService = authService;
        this.clientIpResolver = clientIpResolver;
        this.passwordStrengthEvaluator = passwordStrengthEvaluator;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpServletRequest){
        AuthResponse response = authService.login(request, clientIpResolver.resolve(httpServletRequest));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/password-strength")
    public ResponseEntity<PasswordStrengthResult> passwordStrength(@Valid @RequestBody PasswordStrengthRequest request) {
        PasswordStrengthResult result = passwordStrengthEvaluator.evaluate(request.getPassword());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/confirm")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.noContent().build();
    }
}
