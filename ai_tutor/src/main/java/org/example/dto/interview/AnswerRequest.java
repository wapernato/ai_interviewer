package org.example.dto.interview;

public class AnswerRequest {

    private Long userId;
    private Long questionId;
    private String textAnswer;

    public AnswerRequest(Long userId, Long questionId, String textAnswer){
        this.userId = userId;
        this.questionId = questionId;
        this.textAnswer = textAnswer;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }
}
