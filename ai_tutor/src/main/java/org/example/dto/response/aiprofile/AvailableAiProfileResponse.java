package org.example.dto.response.aiprofile;

public record AvailableAiProfileResponse (

    Long id,
    String name,
    String description,
    String difficulty,
    boolean hintsEnable
){
}
