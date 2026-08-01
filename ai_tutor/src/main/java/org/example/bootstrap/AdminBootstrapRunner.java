package org.example.bootstrap;

import org.example.config.BootstrapAdminProperties;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapAdminProperties bootstrapAdminProperties;

    public AdminBootstrapRunner(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                BootstrapAdminProperties bootstrapAdminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminProperties = bootstrapAdminProperties;
    }

    @Override
    public void run(ApplicationArguments args){

        if(!bootstrapAdminProperties.enabled()){
            return;
        }

        String email = bootstrapAdminProperties.email();
        String username = bootstrapAdminProperties.username();
        String password = bootstrapAdminProperties.password();
        if (email == null || username == null || password == null ||
                email.isBlank() || username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Параметры bootstrap admin не должны быть пустыми.");
        }

        email = email.trim().toLowerCase();
        username = username.trim();

        if(userRepository.existsByEmail(email)){
            return;
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("Параметр Bootstrap admin username уже существует.");
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setRole(UserRole.ADMIN);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEnabled(true);

        userRepository.save(user);
    }
}