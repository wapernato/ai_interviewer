package org.example.mapper;

import org.example.dto.response.QuestionResponse;
import org.example.model.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {

    public QuestionResponse toResponse(Question question){
        if(question == null){
            return null;
        }

        QuestionResponse questionResponse = new QuestionResponse();

        questionResponse.setId(question.getId());
        questionResponse.setUserId(question.getUser().getId());

        if(question.getTopic() == null){
            questionResponse.setTopicId(null);
        }
        else{
            questionResponse.setTopicId(question.getTopic().getId());
        }


        questionResponse.setTextQuestion(question.getTextQuestion());
        questionResponse.setSource(question.getSource());
        questionResponse.setLanguage(question.getLanguage());

        return questionResponse;
    }

    public List<QuestionResponse> toResponseList(List<Question> questions) {
        if (questions == null) {
            return List.of();
        }

        return questions.stream()
                .map(this::toResponse)
                .toList();
    }
}
