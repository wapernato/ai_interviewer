package org.example.security;


import org.example.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Character.*;

@Component
public class PasswordPolicyValidator {

    private record PasswordRule(boolean valid,
                                String message)
    {}

    public void validate(String password) {

        if(password == null || password.isBlank()){
            throw new BadRequestException("Пароль не может быть пустым.");
        }

        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;
        boolean hasWhitespace = false;

        for(char symbol: password.toCharArray()) {
            if(isWhitespace(symbol)) {
                hasWhitespace = true;
            }
            else if(isUpperCase(symbol)) {
                hasUppercase = true;
            }
            else if(isLowerCase(symbol)) {
                hasLowercase = true;
            }
            else if(isDigit(symbol)) {
                hasDigit = true;
            }
            else {
                hasSpecialCharacter = true;
            }
        }
        List<PasswordRule> rules = List.of(
                new PasswordRule(!hasWhitespace, "Пароль не должен содержать пробел."),
                new PasswordRule(hasUppercase, "Пароль должен содержать заглавную букву."),
                new PasswordRule(hasLowercase, "Пароль должен содержать строчную букву."),
                new PasswordRule(hasDigit, "Пароль должен содержать хотя бы одну цифру."),
                new PasswordRule(hasSpecialCharacter, "Пароль должен содержать специальный символ.")
        );

        for(PasswordRule rule : rules) {
            if(!rule.valid()) {
                throw new BadRequestException(rule.message());
            }
        }
    }
}
