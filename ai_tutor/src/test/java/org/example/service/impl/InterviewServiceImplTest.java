package org.example.service.impl;

import org.example.dto.interview.InterviewQuestionResult;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.AiProfile;
import org.example.model.Question;
import org.example.model.Topic;
import org.example.model.User;
import org.example.repository.*;
import org.example.service.AiAnswerEvaluator;
import org.example.service.AiQuestionGenerator;
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

        assertThatThrownBy(() -> interviewService.generateQuestion(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь не найден.");
    }

    @Test
    void generateQuestion_shouldThrowNotFound_whenTopicDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interviewService.generateQuestion(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема не найдена.");

        verify(userRepository).findById(1L);
        verify(topicRepository).findById(1L);

        verifyNoInteractions(
                aiProfileRepository,
                aiQuestionGenerator,
                questionRepository
        );
    }

    @Test
    void generateQuestion_shouldThrowNotFound_whenAiProfilesDoesNotExist(){
        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Topic savedTopic = new Topic("Java Core");
        savedTopic.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));
        when(aiProfileRepository.findFirstByActiveTrue()).thenReturn(Optional.empty());


        assertThatThrownBy(() -> interviewService.generateQuestion(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Активный AI-профиль не найден.");

    }

    @Test
    void generateQuestion_shouldThrowBadRequest_whenGeneratorReturnsBlankText(){
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
        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));
        when(aiProfileRepository.findFirstByActiveTrue())
                .thenReturn(Optional.of(savedAiProfile));
        when(aiQuestionGenerator.generatedQuestion(savedTopic, savedAiProfile))
                .thenReturn("   ");

        assertThatThrownBy(() -> interviewService
                .generateQuestion(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("AI не смог сгенерировать вопрос.");

        verify(userRepository).findById(1L);
        verify(topicRepository).findById(1L);
        verify(aiProfileRepository).findFirstByActiveTrue();
        verify(aiQuestionGenerator).generatedQuestion(savedTopic, savedAiProfile);

        verifyNoInteractions(questionRepository);
    }

    @Test
    void generateQuestion_shouldGenerationQuestion_whenAllCorrect(){
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
        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));
        when(aiProfileRepository.findFirstByActiveTrue())
                .thenReturn(Optional.of(savedAiProfile));
        when(aiQuestionGenerator.generatedQuestion(savedTopic, savedAiProfile))
                .thenReturn("Вопрос успешно создан.");

        Question savedQuestion = new Question();
        savedQuestion.setId(1L);
        savedQuestion.setTextQuestion("Вопрос успешно создан.");

        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        InterviewQuestionResult result = interviewService.generateQuestion(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getQuestionId()).isEqualTo(1L);
        assertThat(result.getTopicId()).isEqualTo(1L);

        verify(questionRepository)
                .save(argThat(question -> "Вопрос успешно создан.".equals(question.getTextQuestion())));

    }
}
