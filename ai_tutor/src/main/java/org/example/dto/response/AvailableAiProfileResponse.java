package org.example.dto.response;

public record AvailableAiProfileResponse (

    Long id,
    String name,
    String description,
    String difficulty,
    boolean hintsEnable
){
}
