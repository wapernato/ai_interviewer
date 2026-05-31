package org.example.exception;

public class AiProfileAlreadyExistsException extends RuntimeException {
    public AiProfileAlreadyExistsException(String message) {
        super(message);
    }
}
