package org.example.controller;


import jakarta.validation.Valid;
import org.example.dto.interview.AnswerRequest;
import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.interview.QuestionRequest;
import org.example.security.JwtService;
import org.example.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;
    private final JwtService jwtService;

    public InterviewController(InterviewService interviewService, JwtService jwtService) {
        this.interviewService = interviewService;
        this.jwtService = jwtService;
    }

    @PostMapping("/answer")
    public ResponseEntity<InterviewAnswerResult> answerResult(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AnswerRequest request){
        Long currentUserId = jwtService.extractUserId(jwt);

        InterviewAnswerResult interviewAnswerResult = interviewService.submitUserAnswer(currentUserId, request.getQuestionId(), request.getTextAnswer());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interviewAnswerResult);
    }

    @PostMapping("/question")
    public ResponseEntity<InterviewQuestionResult> questionResult(@Valid @RequestBody QuestionRequest request){
        InterviewQuestionResult interviewQuestionResult = interviewService.generateQuestion(request.getUserId(), request.getTopicId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interviewQuestionResult);
    }

}
