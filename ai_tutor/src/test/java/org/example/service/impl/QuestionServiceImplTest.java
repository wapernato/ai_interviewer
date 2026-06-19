package org.example.service.impl;

import org.example.dto.response.QuestionResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.mapper.QuestionMapper;
import org.example.model.Question;
import org.example.model.Topic;
import org.example.model.User;
import org.example.repository.QuestionRepository;
import org.example.repository.TopicRepository;
import org.example.repository.UserRepository;
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
public class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private UserRepository userRepository;

    private QuestionMapper questionMapper;

    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp(){
        questionMapper = new QuestionMapper();

        questionService = new QuestionServiceImpl(
                questionRepository,
                topicRepository,
                userRepository,
                questionMapper
        );
    }

    @Test
    void addQuestion_shouldThrowBadRequest_whenQuestionTextNull(){
        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст вопроса не может быть пустым.");

        verifyNoInteractions(userRepository, topicRepository, questionRepository);
    }

    @Test
    void addQuestion_shouldThrowBadRequest_whenQuestionTextIsBlank(){
        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст вопроса не может быть пустым.");

        verifyNoInteractions(userRepository, topicRepository, questionRepository);
    }

    @Test
    void addQuestion_shouldThrowBadRequest_whenQuestionTextTooShort(){
        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, " a "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина вопроса должна быть от 3 до 1000 символов.");

        verifyNoInteractions(userRepository, topicRepository, questionRepository);
    }

    @Test
    void addQuestion_shouldThrowBadRequest_whenQuestionTextTooLong(){
        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, "a".repeat(1001)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина вопроса должна быть от 3 до 1000 символов.");

        verifyNoInteractions(userRepository, topicRepository, questionRepository);
    }

    @Test
    void addQuestion_shouldThrowNotFound_whenUserIdDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, "wkjef we fqw wefw."))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id = 1 не найден.");
    }

    @Test
    void addQuestion_shouldThrowNotFound_whenTopicIdDoesNotExist(){

        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.addQuestion(1L, 1L, "wkjef we fqw wefw."))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с таким id=1 не существует.");
    }

    @Test
    void addQuestion_shouldReturnQuestionResponse_whenDataIsValid(){

        User savedUser = new User("Yakov");
        savedUser.setId(1L);

        Topic savedTopic = new Topic();
        savedTopic.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(savedTopic));

        Question savedQuestion = new Question();

        savedQuestion.setId(1L);
        savedQuestion.setTopic(savedTopic);
        savedQuestion.setUser(savedUser);
        savedQuestion.setTextQuestion("What is JVM?");
        savedQuestion.setSource("manual");
        savedQuestion.setLanguage("ru");

        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        QuestionResponse questionResponse = questionService.addQuestion(1L, 1L, "  What is JVM?  ");

        assertThat(questionResponse).isNotNull();
        assertThat(questionResponse.getTextQuestion()).isEqualTo("What is JVM?");
        assertThat(questionResponse.getId()).isEqualTo(1L);
        assertThat(questionResponse.getUserId()).isEqualTo(1L);
        assertThat(questionResponse.getTopicId()).isEqualTo(1L);
        assertThat(questionResponse.getSource()).isEqualTo("manual");
        assertThat(questionResponse.getLanguage()).isEqualTo("ru");

        verify(questionRepository).save(argThat(question ->
                savedUser.equals(question.getUser())
                        && savedTopic.equals(question.getTopic())
                        && "What is JVM?".equals(question.getTextQuestion())
                        && "manual".equals(question.getSource())
                        && "ru".equals(question.getLanguage())
        ));
    }

    @Test
    void getById_shouldReturnQuestion_whenQuestionExists(){
        Question question = createQuestion(1L, "What is JVM?");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        QuestionResponse result = questionService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTopicId()).isEqualTo(1L);
        assertThat(result.getTextQuestion()).isEqualTo("What is JVM?");
    }

    @Test
    void getById_shouldThrowNotFound_whenQuestionDoesNotExist(){
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вопрос с таким id = 1 не найден.");
    }

    @Test
    void getByTopicId_shouldThrowNotFound_whenTopicDoesNotExist(){
        when(topicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getByTopicId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Тема с id=1 не найдена.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void getByTopicId_shouldReturnQuestions_whenTopicExists(){
        Topic topic = createTopic(1L);
        List<Question> questions = List.of(
                createQuestion(1L, "What is JVM?"),
                createQuestion(2L, "What is JIT?")
        );

        when(topicRepository.findById(1L)).thenReturn(Optional.of(topic));
        when(questionRepository.findByTopic_Id(1L)).thenReturn(questions);

        List<QuestionResponse> result = questionService.getByTopicId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(QuestionResponse::getTextQuestion)
                .containsExactly("What is JVM?", "What is JIT?");
    }

    @Test
    void getByTopicId_shouldReturnEmptyList_whenTopicHasNoQuestions(){
        when(topicRepository.findById(1L)).thenReturn(Optional.of(createTopic(1L)));
        when(questionRepository.findByTopic_Id(1L)).thenReturn(List.of());

        List<QuestionResponse> result = questionService.getByTopicId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getByUserId_shouldThrowNotFound_whenUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getByUserId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id=1 не найден.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void getByUserId_shouldReturnQuestions_whenUserExists(){
        User user = createUser(1L);
        List<Question> questions = List.of(
                createQuestion(1L, "What is JVM?"),
                createQuestion(2L, "What is JIT?")
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionRepository.findByUser_Id(1L)).thenReturn(questions);

        List<QuestionResponse> result = questionService.getByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(QuestionResponse::getTextQuestion)
                .containsExactly("What is JVM?", "What is JIT?");
    }

    @Test
    void getByUserId_shouldReturnEmptyList_whenUserHasNoQuestions(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L)));
        when(questionRepository.findByUser_Id(1L)).thenReturn(List.of());

        List<QuestionResponse> result = questionService.getByUserId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllQuestions_shouldReturnAllQuestions(){
        when(questionRepository.findAll()).thenReturn(List.of(
                createQuestion(1L, "What is JVM?"),
                createQuestion(2L, "What is JIT?")
        ));

        List<QuestionResponse> result = questionService.getAllQuestions();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(QuestionResponse::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void getAllQuestions_shouldReturnEmptyList_whenQuestionsDoNotExist(){
        when(questionRepository.findAll()).thenReturn(List.of());

        List<QuestionResponse> result = questionService.getAllQuestions();

        assertThat(result).isEmpty();
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenNewTextIsNull(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, null, "manual", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Новый текст вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenNewTextIsBlank(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, "   ", "manual", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Новый текст вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenSourceIsNull(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, "What is JIT?", null, "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Source вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenSourceIsBlank(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, "What is JIT?", "   ", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Source вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenLanguageIsNull(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, "What is JIT?", "manual", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Language вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenLanguageIsBlank(){
        assertThatThrownBy(() -> questionService.updateQuestion(1L, "What is JIT?", "manual", "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Language вопроса не может быть пустым.");

        verifyNoInteractions(questionRepository);
    }

    @Test
    void updateQuestion_shouldThrowNotFound_whenQuestionDoesNotExist(){
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.updateQuestion(1L, "What is JIT?", "manual", "ru"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Вопрос с id = 1 не найден.");

        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenNewTextIsTooShort(){
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(createQuestion(1L, "What is JVM?")));

        assertThatThrownBy(() -> questionService.updateQuestion(1L, " a ", "manual", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина вопроса должна быть от 3 до 1000 символов.");

        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenNewTextIsTooLong(){
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(createQuestion(1L, "What is JVM?")));

        assertThatThrownBy(() -> questionService.updateQuestion(1L, "a".repeat(1001), "manual", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина вопроса должна быть от 3 до 1000 символов.");

        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void updateQuestion_shouldThrowBadRequest_whenNewTextEqualsOldText(){
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(createQuestion(1L, "What is JVM?")));

        assertThatThrownBy(() -> questionService.updateQuestion(1L, "  What is JVM?  ", "manual", "ru"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст не должен совпадать со старым текстом.");

        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void updateQuestion_shouldReturnUpdatedQuestion_whenDataIsValid(){
        Question existingQuestion = createQuestion(1L, "What is JVM?");
        when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
        when(questionRepository.save(any(Question.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QuestionResponse result = questionService.updateQuestion(
                1L,
                "  What is JIT?  ",
                "  ai  ",
                "  en  "
        );

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTextQuestion()).isEqualTo("What is JIT?");
        assertThat(result.getSource()).isEqualTo("ai");
        assertThat(result.getLanguage()).isEqualTo("en");

        verify(questionRepository).save(argThat(question ->
                question == existingQuestion
                        && "What is JIT?".equals(question.getTextQuestion())
                        && "ai".equals(question.getSource())
                        && "en".equals(question.getLanguage())
        ));
    }

    @Test
    void deleteById_shouldThrowNotFound_whenQuestionDoesNotExist(){
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.deleteById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Указанный Id=1 не найден в базе данных.");

        verify(questionRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_shouldDeleteQuestion_whenQuestionExists(){
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(createQuestion(1L, "What is JVM?")));

        questionService.deleteById(1L);

        verify(questionRepository).deleteById(1L);
    }

    private User createUser(Long id){
        User user = new User("Yakov");
        user.setId(id);
        return user;
    }

    private Topic createTopic(Long id){
        Topic topic = new Topic("Java Core");
        topic.setId(id);
        return topic;
    }

    private Question createQuestion(Long id, String text){
        Question question = new Question();
        question.setId(id);
        question.setUser(createUser(1L));
        question.setTopic(createTopic(1L));
        question.setTextQuestion(text);
        question.setSource("manual");
        question.setLanguage("ru");
        return question;
    }

}
