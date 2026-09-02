package org.example.service;

import org.example.model.User;

public interface EmailVerificationService {

    String createVerificationToken(User user);

    void verifyEmail(String token);

}
