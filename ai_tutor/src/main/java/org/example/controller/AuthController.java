package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.PasswordStrengthRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.dto.response.AuthResponse;
import org.example.security.ClientIpResolver;
import org.example.security.PasswordStrengthEvaluator;
import org.example.security.PasswordStrengthResult;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ClientIpResolver clientIpResolver;
    private final PasswordStrengthEvaluator passwordStrengthEvaluator;

    public AuthController(AuthService authService,
                          ClientIpResolver clientIpResolver,
                          PasswordStrengthEvaluator passwordStrengthEvaluator){
        this.authService = authService;
        this.clientIpResolver = clientIpResolver;
        this.passwordStrengthEvaluator = passwordStrengthEvaluator;
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
    public ResponseEntity<PasswordStrengthResult> passwordStrength(@RequestBody PasswordStrengthRequest request) {
        PasswordStrengthResult result = passwordStrengthEvaluator.evaluate(request.getPassword());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }




}
