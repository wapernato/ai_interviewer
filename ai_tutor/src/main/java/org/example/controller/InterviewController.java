package org.example.controller;


import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.service.InterviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/answer")
    public InterviewAnswerResult answerResult(@RequestBody AnswerRequest request){
        return interviewService.submitUserAnswer(request.getUserId(), request.getQuestionId(), request.getTextAnswer());
    }

    @PostMapping("/question")
    public InterviewQuestionResult questionResult(@RequestBody QuestionRequest request){
        return interviewService.generateQuestion(request.getUserId(), request.getTopicId());
    }

}
