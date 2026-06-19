package org.example.service.impl;

import org.example.dto.user.UserHistoryItem;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.repository.UserHistoryRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHistoryServiceImplTest {

    @Mock
    private UserHistoryRepository userHistoryRepository;
    @Mock
    private UserRepository userRepository;

    private UserHistoryServiceImpl userHistoryService;

    @BeforeEach
    void setUp() {
        userHistoryService = new UserHistoryServiceImpl(userHistoryRepository, userRepository);
    }

    @Test
    void findHistoryByUserId_shouldThrowBadRequest_whenUserIdIsInvalid() {
        assertThatThrownBy(() -> userHistoryService.findHistoryByUserId(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Некорректный id.");

        verifyNoInteractions(userRepository, userHistoryRepository);
    }

    @Test
    void findHistoryByUserId_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userHistoryService.findHistoryByUserId(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователя с таким id не существует.");

        verifyNoInteractions(userHistoryRepository);
    }

    @Test
    void findHistoryByUserId_shouldReturnHistory_whenUserExists() {
        List<UserHistoryItem> history = List.of(
                new UserHistoryItem(1L, "Yakov", "Java", "What is JVM?", "Answer", "gpt"),
                new UserHistoryItem(2L, "Yakov", "Spring", "What is DI?", null, null)
        );
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userHistoryRepository.findHistoryByUserId(1L)).thenReturn(history);

        List<UserHistoryItem> result = userHistoryService.findHistoryByUserId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserHistoryItem::getQuestionId).containsExactly(1L, 2L);
        assertThat(result.get(0).getAnswerText()).isEqualTo("Answer");
        assertThat(result.get(1).getAnswerText()).isNull();
    }

    @Test
    void findHistoryByUserId_shouldReturnEmptyList_whenHistoryDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userHistoryRepository.findHistoryByUserId(1L)).thenReturn(List.of());

        assertThat(userHistoryService.findHistoryByUserId(1L)).isEmpty();
    }
}
