package org.example.exception;

public class AnswerAlreadyExistsException extends RuntimeException {
    public AnswerAlreadyExistsException(String message) {
        super(message);
    }
}
