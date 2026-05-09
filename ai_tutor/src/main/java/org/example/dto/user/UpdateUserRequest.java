package org.example.dto.user;

public class UpdateUserRequest {

    private String username;

    public UpdateUserRequest() {}

    public UpdateUserRequest(String newUsername){ this.username = newUsername; }

    public void setNewUsername(String newUsername) {
        this.username = newUsername;
    }

    public String getNewUsername() {
        return username;
    }
}
