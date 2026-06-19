package org.example.service.impl;

import org.example.dto.response.AnswerResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.AnswerMapper;
import org.example.model.AiProfile;
import org.example.model.Answer;
import org.example.model.Question;
import org.example.repository.AiProfileRepository;
import org.example.repository.AnswerRepository;
import org.example.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AiProfileRepository aiProfileRepository;

    private AnswerServiceImpl answerService;

    @BeforeEach
    void setUp() {
        answerService = new AnswerServiceImpl(
                answerRepository,
                questionRepository,
                aiProfileRepository,
                new AnswerMapper()
        );
    }

    @Test
    void addAnswer_shouldThrowBadRequest_whenQuestionIdIsInvalid() {
        assertThatThrownBy(() -> answerService.addAnswer(0L, 1L, "Answer", "gpt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id вопроса не может быть null.");

        verifyNoInteractions(questionRepository, aiProfileRepository, answerRepository);
    }

    @Test
    void addAnswer_shouldThrowBadRequest_whenAiProfileIdIsInvalid() {
        assertThatThrownBy(() -> answerService.addAnswer(1L, null, "Answer", "gpt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id профиля не может быть null.");

        verifyNoInteractions(questionRepository, aiProfileRepository, answerRepository);
    }

    @Test
    void addAnswer_shouldThrowBadRequest_whenAnswerTextIsBlank() {
        assertThatThrownBy(() -> answerService.addAnswer(1L, 1L, "   ", "gpt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст ответа не может быть пустым.");

        verifyNoInteractions(questionRepository, aiProfileRepository, answerRepository);
    }

    @Test
    void addAnswer_shouldThrowBadRequest_whenModelNameIsNull() {
        assertThatThrownBy(() -> answerService.addAnswer(1L, 1L, "Answer", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название модели не может быть пустым.");

        verifyNoInteractions(questionRepository, aiProfileRepository, answerRepository);
    }

    @Test
    void addAnswer_shouldThrowNotFound_whenQuestionDoesNotExist() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.addAnswer(1L, 1L, "Answer", "gpt"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вопрос с id=1 не найден.");

        verifyNoInteractions(aiProfileRepository, answerRepository);
    }

    @Test
    void addAnswer_shouldThrowNotFound_whenAiProfileDoesNotExist() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(createQuestion(1L)));
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.addAnswer(1L, 1L, "Answer", "gpt"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с id=1 не найден.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void addAnswer_shouldReturnAnswerResponse_whenDataIsValid() {
        Question question = createQuestion(1L);
        AiProfile profile = createProfile(1L);
        Answer savedAnswer = createAnswer(1L, question, profile, "Detailed answer", "gpt");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(aiProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        AnswerResponse result = answerService.addAnswer(1L, 1L, "  Detailed answer  ", "  gpt  ");

        assertAnswer(result, 1L, "Detailed answer", "gpt");
        verify(answerRepository).save(argThat(answer ->
                question.equals(answer.getQuestion())
                        && profile.equals(answer.getAiProfile())
                        && "Detailed answer".equals(answer.getAnswerText())
                        && "gpt".equals(answer.getModelName())
        ));
    }

    @Test
    void updateAnswer_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> answerService.updateAnswer(null, "Answer", "gpt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id ответа не может быть null.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void updateAnswer_shouldThrowBadRequest_whenAnswerTextIsNull() {
        assertThatThrownBy(() -> answerService.updateAnswer(1L, null, "gpt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст ответа не может быть пустым.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void updateAnswer_shouldThrowBadRequest_whenModelNameIsBlank() {
        assertThatThrownBy(() -> answerService.updateAnswer(1L, "Answer", "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Название модели не может быть пустым.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void updateAnswer_shouldThrowNotFound_whenAnswerDoesNotExist() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.updateAnswer(1L, "Answer", "gpt"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ответ с id=1 не найден.");

        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void updateAnswer_shouldReturnUpdatedAnswer_whenDataIsValid() {
        Answer answer = createAnswer(1L, createQuestion(1L), createProfile(1L), "Old", "old-model");
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnswerResponse result = answerService.updateAnswer(1L, "  New answer  ", "  new-model  ");

        assertAnswer(result, 1L, "New answer", "new-model");
        verify(answerRepository).save(argThat(saved ->
                saved == answer
                        && "New answer".equals(saved.getAnswerText())
                        && "new-model".equals(saved.getModelName())
        ));
    }

    @Test
    void deleteById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> answerService.deleteById(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id ответа не может быть null.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void deleteById_shouldThrowNotFound_whenAnswerDoesNotExist() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.deleteById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ответ с id=1 не найден.");

        verify(answerRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_shouldDeleteAnswer_whenAnswerExists() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(createAnswer(1L)));

        answerService.deleteById(1L);

        verify(answerRepository).deleteById(1L);
    }

    @Test
    void getById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> answerService.getById(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id ответа не может быть null.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void getById_shouldThrowNotFound_whenAnswerDoesNotExist() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> answerService.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Ответ с id=1 не найден.");
    }

    @Test
    void getById_shouldReturnAnswer_whenAnswerExists() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(createAnswer(1L)));

        AnswerResponse result = answerService.getById(1L);

        assertAnswer(result, 1L, "Answer", "gpt");
    }

    @Test
    void getByQuestionId_shouldThrowBadRequest_whenQuestionIdIsInvalid() {
        assertThatThrownBy(() -> answerService.getByQuestionId(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id вопроса не может быть null.");

        verifyNoInteractions(questionRepository, answerRepository);
    }

    @Test
    void getByQuestionId_shouldThrowNotFound_whenQuestionDoesNotExist() {
        when(questionRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> answerService.getByQuestionId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вопрос с id=1 не найден.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void getByQuestionId_shouldReturnAnswers_whenQuestionExists() {
        when(questionRepository.existsById(1L)).thenReturn(true);
        when(answerRepository.findByQuestion_Id(1L)).thenReturn(List.of(createAnswer(1L), createAnswer(2L)));

        List<AnswerResponse> result = answerService.getByQuestionId(1L);

        assertThat(result).extracting(AnswerResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void getByProfileId_shouldThrowBadRequest_whenProfileIdIsInvalid() {
        assertThatThrownBy(() -> answerService.getByProfileId(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Id AI-профиля не может быть null.");

        verifyNoInteractions(aiProfileRepository, answerRepository);
    }

    @Test
    void getByProfileId_shouldThrowNotFound_whenProfileDoesNotExist() {
        when(aiProfileRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> answerService.getByProfileId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("AI-профиль с id=1 не найден.");

        verifyNoInteractions(answerRepository);
    }

    @Test
    void getByProfileId_shouldReturnAnswers_whenProfileExists() {
        when(aiProfileRepository.existsById(1L)).thenReturn(true);
        when(answerRepository.findByAiProfile_Id(1L)).thenReturn(List.of(createAnswer(1L), createAnswer(2L)));

        List<AnswerResponse> result = answerService.getByProfileId(1L);

        assertThat(result).extracting(AnswerResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void getAllAnswers_shouldReturnAllAnswers() {
        when(answerRepository.findAll()).thenReturn(List.of(createAnswer(1L), createAnswer(2L)));

        List<AnswerResponse> result = answerService.getAllAnswers();

        assertThat(result).extracting(AnswerResponse::getId).containsExactly(1L, 2L);
    }

    @Test
    void getAllAnswers_shouldReturnEmptyList_whenAnswersDoNotExist() {
        when(answerRepository.findAll()).thenReturn(List.of());

        assertThat(answerService.getAllAnswers()).isEmpty();
    }

    private void assertAnswer(AnswerResponse result, Long id, String text, String modelName) {
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getQuestionId()).isEqualTo(1L);
        assertThat(result.getAiProfileId()).isEqualTo(1L);
        assertThat(result.getAnswerText()).isEqualTo(text);
        assertThat(result.getModelName()).isEqualTo(modelName);
    }

    private Answer createAnswer(Long id) {
        return createAnswer(id, createQuestion(1L), createProfile(1L), "Answer", "gpt");
    }

    private Answer createAnswer(Long id, Question question, AiProfile profile, String text, String modelName) {
        Answer answer = new Answer();
        answer.setId(id);
        answer.setQuestion(question);
        answer.setAiProfile(profile);
        answer.setAnswerText(text);
        answer.setModelName(modelName);
        return answer;
    }

    private Question createQuestion(Long id) {
        Question question = new Question();
        question.setId(id);
        return question;
    }

    private AiProfile createProfile(Long id) {
        AiProfile profile = new AiProfile();
        profile.setId(id);
        return profile;
    }
}
