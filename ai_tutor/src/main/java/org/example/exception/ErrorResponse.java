package org.example.exception;

public class ErrorResponse {

    private int status;
    private String message;
    private String error;

    public ErrorResponse() {}

    public ErrorResponse(int status, String error, String message){
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
