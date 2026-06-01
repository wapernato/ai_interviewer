package org.example.controller;


import jakarta.validation.Valid;
import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/answer")
    public ResponseEntity<InterviewAnswerResult> answerResult(@Valid @RequestBody AnswerRequest request){
        InterviewAnswerResult interviewAnswerResult = interviewService.submitUserAnswer(request.getUserId(), request.getQuestionId(), request.getTextAnswer());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewAnswerResult);
    }

    @PostMapping("/question")
    public ResponseEntity<InterviewQuestionResult> questionResult(@Valid @RequestBody QuestionRequest request){
        InterviewQuestionResult interviewQuestionResult = interviewService.generateQuestion(request.getUserId(), request.getTopicId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(interviewQuestionResult);
    }

}
