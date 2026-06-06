package org.example.exception;

import java.util.Map;

public class ValidationErrorResponse {

    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(int status,
                                   String error,
                                   String message,
                                   Map<String, String> validationErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}