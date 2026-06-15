package org.example.mapper;

import org.example.dto.response.AnswerResponse;
import org.example.model.Answer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnswerMapper {

    public AnswerResponse toResponse(Answer answer){

        if(answer == null){
            return null;
        }

        AnswerResponse answerResponse = new AnswerResponse();

        answerResponse.setId(answer.getId());

        if(answer.getAiProfile() == null){
            answerResponse.setAiProfileId(null);
        }else {
            answerResponse.setAiProfileId(answer.getAiProfile().getId());
        }

        answerResponse.setModelName(answer.getModelName());
        answerResponse.setQuestionId(answer.getQuestion().getId());
        answerResponse.setAnswerText(answer.getAnswerText());


        return answerResponse;
    }

    public List<AnswerResponse> toResponseList(List<Answer> answers){
        if(answers == null){
            return List.of();
        }

        return answers.stream()
                .map(this::toResponse)
                .toList();
    }
}
