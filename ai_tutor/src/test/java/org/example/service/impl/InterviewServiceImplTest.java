package org.example.service.impl;

import org.example.dto.interview.InterviewAnswerResult;
import org.example.dto.interview.InterviewQuestionResult;
import org.example.dto.ai.QuestionGenerationResult;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.integration.ai.AiQuestionGenerator;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.AiAnswerEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceImplTest {

    @Mock
    private AiQuestionGenerator aiQuestionGenerator;

    @Mock
    private AiAnswerEvaluator aiAnswerEvaluator;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private AiProfileRepository aiProfileRepository;

    @Mock
    private AnswerRepository answerRepository;

    private InterviewServiceImpl interviewService;

    @BeforeEach
    void setUp(){
        interviewService = new InterviewServiceImpl(
                aiQuestionGenerator,
                aiAnswerEvaluator,
                questionRepository,
                userRepository,
                topicRepository,
                aiProfileRepository,
                answerRepository
        );
    }

    @Test
    void generateQuestion_shouldThrowNotFound_whenUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.generateQuestion(1L, "Java Core"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");
    }

    @Test
    void generateQuestion_shouldThrowNotFound_whenAiProfilesDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.empty());


        assertThatThrownBy(() -> interviewService.generateQuestion(1L, "Java Core"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Активный AI-профиль не найден.");

        verifyNoInteractions(aiQuestionGenerator, topicRepository, questionRepository);
    }

    @Test
    void generateQuestion_shouldThrowBadRequest_whenGeneratorRejectsTopic(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        AiProfile savedAiProfile = new AiProfile(
                1L,
                "mode",
                "description mode",
                "instruction mode",
                "model name",
                "language",
                "answer style",
                "difficulty",
                "feedback mode",
                true,
                true,
                0.7,
                2000

        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(aiProfileRepository.findFirstByActiveTrue())
                .thenReturn(Optional.of(savedAiProfile));
        when(aiQuestionGenerator.generate("   ", savedAiProfile))
                .thenThrow(new BadRequestException("Текст темы не должен быть пустым."));

        assertThatThrownBy(() -> interviewService
                .generateQuestion(1L, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст темы не должен быть пустым.");

        verify(userRepository).findById(1L);
        verify(aiProfileRepository).findFirstByActiveTrue();
        verify(aiQuestionGenerator).generate("   ", savedAiProfile);

        verifyNoInteractions(topicRepository, questionRepository);
    }

    @Test
    void generateQuestion_shouldReturnGeneratedQuestion_whenAllDataIsValid(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Topic savedTopic = new Topic("Java Core");
        savedTopic.setId(1L);

        AiProfile savedAiProfile = new AiProfile(
                1L,
                "mode",
                "description mode",
                "instruction mode",
                "model name",
                "language",
                "answer style",
                "difficulty",
                "feedback mode",
                true,
                true,
                0.7,
                2000

        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(aiProfileRepository.findFirstByActiveTrue())
                .thenReturn(Optional.of(savedAiProfile));

        QuestionGenerationResult generationResult = new QuestionGenerationResult();
        generationResult.setQuestionText("Вопрос успешно создан.");
        generationResult.setNormalizedTopic("Java Core");
        generationResult.setDifficulty(QuestionDifficulty.MEDIUM);

        when(aiQuestionGenerator.generate(" Java Core ", savedAiProfile))
                .thenReturn(generationResult);
        when(topicRepository.findByName("Java Core"))
                .thenReturn(Optional.of(savedTopic));

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setTextQuestion("Вопрос успешно создан.");
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTopic(savedTopic);
        savedQuestion.setAiProfile(savedAiProfile);
        savedQuestion.setDifficulty(QuestionDifficulty.MEDIUM);

        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        InterviewQuestionResult result = interviewService.generateQuestion(1L, " Java Core ");

        assertThat(result).isNotNull();
        assertThat(result.getQuestionId()).isEqualTo(1L);
        assertThat(result.getTopicId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getAiProfileId()).isEqualTo(1L);
        assertThat(result.getTopicName()).isEqualTo("Java Core");
        assertThat(result.getQuestionText()).isEqualTo("Вопрос успешно создан.");
        assertThat(result.getAiMode()).isEqualTo("mode");
        assertThat(result.getDifficulty()).isEqualTo(QuestionDifficulty.MEDIUM);

        verify(aiQuestionGenerator).generate(" Java Core ", savedAiProfile);
        verify(topicRepository).findByName("Java Core");
        verify(questionRepository).save(argThat(question ->
                "Вопрос успешно создан.".equals(question.getTextQuestion())
                        && question.getCreatedByUser().equals(savedUser)
                        && question.getTopic().equals(savedTopic)
                        && question.getAiProfile().equals(savedAiProfile)
                        && question.getDifficulty().equals(QuestionDifficulty.MEDIUM)
                        && "ai".equals(question.getSource())
                        && "ru".equals(question.getLanguage())
        ));
    }

    @Test
    void generateQuestion_shouldCreateTopic_whenGeneratedTopicDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        AiProfile savedAiProfile = new AiProfile(
                1L,
                "mode",
                "description mode",
                "instruction mode",
                "model name",
                "language",
                "answer style",
                "difficulty",
                "feedback mode",
                true,
                true,
                0.7,
                2000
        );

        QuestionGenerationResult generationResult = new QuestionGenerationResult();
        generationResult.setQuestionText("Вопрос по Docker.");
        generationResult.setNormalizedTopic("Docker");
        generationResult.setDifficulty(QuestionDifficulty.EASY);

        Topic savedTopic = new Topic("Docker");
        savedTopic.setId(5L);

        Question savedQuestion = new Question();
        savedQuestion.setId(10L);
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTopic(savedTopic);
        savedQuestion.setAiProfile(savedAiProfile);
        savedQuestion.setTextQuestion("Вопрос по Docker.");
        savedQuestion.setDifficulty(QuestionDifficulty.EASY);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.of(savedAiProfile));
        when(aiQuestionGenerator.generate("Docker", savedAiProfile)).thenReturn(generationResult);
        when(topicRepository.findByName("Docker")).thenReturn(Optional.empty());
        when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);
        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        InterviewQuestionResult result = interviewService.generateQuestion(1L, "Docker");

        assertThat(result.getTopicId()).isEqualTo(5L);
        assertThat(result.getTopicName()).isEqualTo("Docker");
        assertThat(result.getDifficulty()).isEqualTo(QuestionDifficulty.EASY);

        verify(topicRepository).save(argThat(topic -> "Docker".equals(topic.getName())));
    }

    @Test
    void submitUserAnswer_shouldThrowNotFound_whenUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, "It is algo quick sort"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");

        verifyNoInteractions(questionRepository);
        verifyNoInteractions(aiProfileRepository);
        verifyNoInteractions(aiAnswerEvaluator);
        verifyNoInteractions(answerRepository);
    }

    @Test
    void submitUserAnswer_shouldThrowNotFound_whenQuestionDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, "It is algo quick sort" ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вопрос не найден.");

        verifyNoInteractions(aiProfileRepository);
        verifyNoInteractions(aiAnswerEvaluator);
        verifyNoInteractions(answerRepository);
    }

    @Test
    void submitUserAnswer_shouldThrowBadRequest_whenQuestionBelongsToAnotherUser(){
        User savedUser1 = new User("Yakov");
        savedUser1.setId(1L);

        User savedUser2 = new User("Rodion");
        savedUser2.setId(2L);

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setCreatedByUser(savedUser2);
        savedQuestion.setTextQuestion("Java Core");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser1));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(savedQuestion));

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, "It is algo quick sort"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Нельзя ответить на вопрос другого пользователя.");

        verifyNoInteractions(aiProfileRepository, aiAnswerEvaluator, answerRepository);
    }

    @Test
    void submitUserAnswer_shouldThrowBadRequest_whenAnswerIsBlank(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTextQuestion("Java Core");


        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(savedQuestion));

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ответ не может быть пустой.");
    }

    @Test
    void submitUserAnswer_shouldThrowBadRequest_whenAnswerIsNull(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTextQuestion("Java Core");


        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(savedQuestion));

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Ответ не может быть пустой.");
    }

    @Test
    void submitUserAnswer_shouldThrowNotFound_whenAiProfileDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTextQuestion("Java Core");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(savedQuestion));
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.submitUserAnswer(1L, 1L, "It is algo quick sort"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Активный AI-профиль не найден.");
    }

    @Test
    void submitUserAnswer_shouldReturnInterviewAnswerResult_whenDataIsValid(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setCreatedByUser(savedUser);
        savedQuestion.setTextQuestion("Java Core");

        AiProfile savedAiProfile = new AiProfile(
                1L,
                "mode",
                "description mode",
                "instruction mode",
                "model name",
                "language",
                "answer style",
                "difficulty",
                "feedback mode",
                true,
                true,
                0.7,
                2000

        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(savedQuestion));
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.of(savedAiProfile));

        String userAnswerText = "   Answer is saluki 06.09.2026   ";
        String normalizedUserAnswerText = "Answer is saluki 06.09.2026";
        String feedbackText = "Good answer.";

        Answer savedAnswer = new Answer();
        savedAnswer.setId(1L);
        savedAnswer.setAnswerText(normalizedUserAnswerText);
        savedAnswer.setQuestion(savedQuestion);
        savedAnswer.setAiProfile(savedAiProfile);
        savedAnswer.setModelName(savedAiProfile.getModelName());

        when(aiAnswerEvaluator.evaluateAnswer(savedQuestion, savedAiProfile, normalizedUserAnswerText))
                .thenReturn(feedbackText);
        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        InterviewAnswerResult result = interviewService.submitUserAnswer(1L, 1L, userAnswerText);

        assertThat(result).isNotNull();
        assertThat(result.getAnswerId()).isEqualTo(1L);
        assertThat(result.getQuestionId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserAnswerText()).isEqualTo(normalizedUserAnswerText);
        assertThat(result.getQuestionText()).isEqualTo("Java Core");
        assertThat(result.getFeedback()).isEqualTo(feedbackText);

        verify(aiAnswerEvaluator).evaluateAnswer(savedQuestion, savedAiProfile, normalizedUserAnswerText);
        verify(answerRepository).save(argThat(answer ->
                normalizedUserAnswerText.equals(answer.getAnswerText())
                        && savedQuestion.equals(answer.getQuestion())
                        && savedAiProfile.equals(answer.getAiProfile())
                        && savedAiProfile.getModelName().equals(answer.getModelName())
        ));
    }

}
