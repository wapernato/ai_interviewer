package org.example.security;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PasswordStrengthEvaluator {

    private static final int MAX_REPEATED_CHARACTER_RUN = 3;
    private static final int KEYBOARD_SEQUENCE_LENGTH = 4;
    private static final int MIN_RECOMMENDED_LENGTH = 12;
    private static final int STRONG_RECOMMENDED_LENGTH = 16;
    private static final int BONUS_POINTS = 10;
    private static final int PENALTY_POINTS = 15;
    private static final List<String> KEYBOARD_ROWS = List.of(
            "1234567890",
            "qwertyuiop[]",
            "asdfghjkl;'",
            "zxcvbnm,./"
    );

    private boolean hasRepeatedCharacters(String password) {
        if(password == null || password.isBlank()){
            return false;
        }
        int count = 1;
        char c1 = password.charAt(0);
        for(int i = 1; i < password.length(); i++){
            char c2 = password.charAt(i);
            if(c1 == c2){
                count++;
            }
            else{
                c1 = password.charAt(i);
                count = 1;
            }

            if(count == MAX_REPEATED_CHARACTER_RUN){
                return true;
            }
        }

        return false;
    }

    private boolean hasSimpleSequence(String password) {
        if(password == null || password.isBlank() || password.length() < 4) {
            return false;
        }

        int diff;
        int ascendingRunLength = 1;
        int descendingRunLength = 1;

        String normalizedPassword = password.toLowerCase();
        for(int i = 1; i < password.length(); i++){
            char curChar = normalizedPassword.charAt(i);
            char prevChar = normalizedPassword.charAt(i - 1);
            diff = curChar - prevChar;
            if(diff == 1) {
                ascendingRunLength++;
                descendingRunLength = 1;
            }
            else if(diff == -1) {
                descendingRunLength++;
                ascendingRunLength = 1;
            }
            else{
                ascendingRunLength = 1;
                descendingRunLength = 1;
            }

            if(ascendingRunLength >= 4 || descendingRunLength >= 4){
                return true;
            }
        }

        return false;
    }

    private boolean hasKeyboardSequence(String password) {
        if(password == null || password.length() < KEYBOARD_SEQUENCE_LENGTH) {
            return false;
        }

        String normalizedPassword = password.toLowerCase();

        for(String row : KEYBOARD_ROWS) {
            if(containsKeyboardSequence(normalizedPassword, row)) {
                return true;
            }

            String reversedRow = new StringBuilder(row).reverse().toString();
            if(containsKeyboardSequence(normalizedPassword, reversedRow)) {
                return true;
            }
        }

        return false;
    }

    private PasswordStrengthLevel resolveLevel(int score) {
        if(score < 30) {
            return PasswordStrengthLevel.WEAK;
        }

        if(score < 60) {
            return PasswordStrengthLevel.MEDIUM;
        }

        return PasswordStrengthLevel.STRONG;
    }

    private boolean containsKeyboardSequence(String password, String keyboardRow) {
        for(int i = 0; i <= keyboardRow.length() - KEYBOARD_SEQUENCE_LENGTH; i++) {
            String sequence = keyboardRow.substring(i, i + KEYBOARD_SEQUENCE_LENGTH);

            if(password.contains(sequence)) {
                return true;
            }
        }

        return false;
    }

    public PasswordStrengthResult evaluate(String password) {
        if(password == null || password.isBlank()) {
            return new PasswordStrengthResult(
                    PasswordStrengthLevel.WEAK,
                    List.of("Введите пароль.")
            );
        }

        int score = 0;
        List<String> suggestions = new ArrayList<>();

        if(password.length() >= 8){
            score += BONUS_POINTS;
        }

        if(password.length() >= MIN_RECOMMENDED_LENGTH){
            score += BONUS_POINTS;
        }
        else {
            suggestions.add("Используйте пароль длиной не менее 12 символов.");
        }

        if(password.length() >= STRONG_RECOMMENDED_LENGTH){
            score += BONUS_POINTS;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;
        boolean hasWhitespace = false;

        for(char symbol : password.toCharArray()) {
            if(Character.isWhitespace(symbol)) {
                hasWhitespace = true;
            }
            else if(Character.isUpperCase(symbol)){
                hasUppercase = true;
            }
            else if(Character.isLowerCase(symbol)) {
                hasLowercase = true;
            }
            else if(Character.isDigit(symbol)) {
                hasDigit = true;
            }
            else{
                hasSpecialCharacter = true;
            }
        }

        if(hasLowercase) {
            score += BONUS_POINTS;
        }
        else {
            suggestions.add("Добавьте хотя бы одну строчную букву.");
        }

        if(hasUppercase) {
            score += BONUS_POINTS;
        }
        else {
            suggestions.add("Добавьте хотя бы одну заглавную букву.");
        }

        if(hasDigit) {
            score += BONUS_POINTS;
        }
        else {
            suggestions.add("Добавьте хотя бы одну цифру.");
        }

        if(hasSpecialCharacter) {
            score += BONUS_POINTS;
        }
        else {
            suggestions.add("Добавьте хотя бы один специальный символ.");
        }

        if(hasWhitespace) {
            suggestions.add("Не используйте пробельные символы в пароле.");
        }

        if(hasRepeatedCharacters(password)) {
            score -= PENALTY_POINTS;
            suggestions.add("Избегайте повторения одного символа подряд.");
        }

        if(hasSimpleSequence(password)) {
            score -= PENALTY_POINTS;
            suggestions.add("Избегайте простых последовательностей символов.");
        }

        if(hasKeyboardSequence(password)) {
            score -= PENALTY_POINTS;
            suggestions.add("Избегайте последовательностей на клавиатуре.");
        }

        if(score < 0) {
            score = 0;
        }

        return new PasswordStrengthResult(resolveLevel(score), suggestions);
    }


}
