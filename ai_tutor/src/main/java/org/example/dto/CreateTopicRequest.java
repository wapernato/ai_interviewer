package org.example.dto;

public class CreateTopicRequest {

    private String name;

    public CreateTopicRequest() {}

    public CreateTopicRequest(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
