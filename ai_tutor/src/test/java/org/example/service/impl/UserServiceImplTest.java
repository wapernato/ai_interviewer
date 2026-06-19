package org.example.service.impl;

import org.example.dto.response.UserResponse;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.exception.UserAlreadyExistsException;
import org.example.mapper.UserMapper;
import org.example.model.User;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, new UserMapper());
    }

    @Test
    void register_shouldThrowBadRequest_whenUsernameIsNull() {
        assertThatThrownBy(() -> userService.register(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть пустым.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowBadRequest_whenUsernameIsBlank() {
        assertThatThrownBy(() -> userService.register("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть пустым.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowBadRequest_whenUsernameContainsSpaces() {
        assertThatThrownBy(() -> userService.register("Yakov Dev"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя не должно содержать пробелы.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowBadRequest_whenUsernameIsTooShort() {
        assertThatThrownBy(() -> userService.register("Y"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина имени должна быть от 2 до 50.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowBadRequest_whenUsernameIsTooLong() {
        assertThatThrownBy(() -> userService.register("Y".repeat(51)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина имени должна быть от 2 до 50.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void register_shouldThrowUserAlreadyExists_whenUsernameExists() {
        when(userRepository.findByUsername("Yakov")).thenReturn(Optional.of(createUser(1L, "Yakov")));

        assertThatThrownBy(() -> userService.register("Yakov"))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Пользователь с именем 'Yakov' уже существует.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldReturnUserResponse_whenUsernameIsValid() {
        User savedUser = createUser(1L, "Yakov");
        when(userRepository.findByUsername("Yakov")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = userService.register("  Yakov  ");

        assertUser(result, 1L, "Yakov");
        verify(userRepository).save(argThat(user -> "Yakov".equals(user.getUsername())));
    }

    @Test
    void getById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> userService.getById(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id должен быть больше 0.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void getById_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id=1 не найден.");
    }

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "Yakov")));

        assertUser(userService.getById(1L), 1L, "Yakov");
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                createUser(1L, "Yakov"),
                createUser(2L, "Rodion")
        ));

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).extracting(UserResponse::getUsername).containsExactly("Yakov", "Rodion");
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenUsersDoNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.getAllUsers()).isEmpty();
    }

    @Test
    void updateUsername_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> userService.updateUsername(null, "Yakov"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("id должен быть больше 0.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUsername_shouldThrowBadRequest_whenUsernameIsNull() {
        assertThatThrownBy(() -> userService.updateUsername(1L, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть null.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUsername_shouldThrowBadRequest_whenUsernameIsBlank() {
        assertThatThrownBy(() -> userService.updateUsername(1L, "   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть пустым.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUsername_shouldThrowBadRequest_whenUsernameContainsSpaces() {
        assertThatThrownBy(() -> userService.updateUsername(1L, "Yakov Dev"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя не должно содержать пробелы.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUsername_shouldThrowBadRequest_whenUsernameLengthIsInvalid() {
        assertThatThrownBy(() -> userService.updateUsername(1L, "Y"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина имени должна быть от 2 до 50.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUsername_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUsername(1L, "Yakov"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id=1 не найден.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUsername_shouldThrowUserAlreadyExists_whenNameBelongsToAnotherUser() {
        User updatedUser = createUser(1L, "Yakov");
        User existingUser = createUser(2L, "Rodion");
        when(userRepository.findById(1L)).thenReturn(Optional.of(updatedUser));
        when(userRepository.findByUsername("Rodion")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUsername(1L, "Rodion"))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Пользователь с таким именем уже существует.");

        verify(userRepository, never()).save(any(User.class));
        assertThat(updatedUser.getUsername()).isEqualTo("Yakov");
    }

    @Test
    void updateUsername_shouldAllowNameThatBelongsToSameUser() {
        User user = createUser(1L, "Yakov");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("Yakov")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.updateUsername(1L, "Yakov");

        assertUser(result, 1L, "Yakov");
        verify(userRepository).save(user);
    }

    @Test
    void updateUsername_shouldReturnUpdatedUser_whenDataIsValid() {
        User user = createUser(1L, "Yakov");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("Rodion")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.updateUsername(1L, "  Rodion  ");

        assertUser(result, 1L, "Rodion");
        verify(userRepository).save(argThat(saved -> saved == user && "Rodion".equals(saved.getUsername())));
    }

    @Test
    void deleteById_shouldThrowBadRequest_whenIdIsInvalid() {
        assertThatThrownBy(() -> userService.deleteById(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Такой id некорректный.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id=1 не найден.");

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteById_shouldDeleteUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "Yakov")));

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void findByName_shouldThrowBadRequest_whenUsernameIsNull() {
        assertThatThrownBy(() -> userService.findByName(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть null.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void findByName_shouldThrowBadRequest_whenUsernameIsBlank() {
        assertThatThrownBy(() -> userService.findByName("   "))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя пользователя не должно быть пустым.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void findByName_shouldThrowBadRequest_whenUsernameContainsSpaces() {
        assertThatThrownBy(() -> userService.findByName("Yakov Dev"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Имя не должно содержать пробелы.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void findByName_shouldThrowBadRequest_whenUsernameLengthIsInvalid() {
        assertThatThrownBy(() -> userService.findByName("Y"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Длина имени должна быть от 2 до 50.");
        verifyNoInteractions(userRepository);
    }

    @Test
    void findByName_shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findByUsername("Yakov")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByName("Yakov"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с именем (Yakov) не найден.");
    }

    @Test
    void findByName_shouldReturnUser_whenUserExists() {
        when(userRepository.findByUsername("Yakov")).thenReturn(Optional.of(createUser(1L, "Yakov")));

        UserResponse result = userService.findByName("  Yakov  ");

        assertUser(result, 1L, "Yakov");
        verify(userRepository).findByUsername("Yakov");
    }

    private User createUser(Long id, String username) {
        User user = new User(username);
        user.setId(id);
        return user;
    }

    private void assertUser(UserResponse result, Long id, String username) {
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getUsername()).isEqualTo(username);
    }
}
