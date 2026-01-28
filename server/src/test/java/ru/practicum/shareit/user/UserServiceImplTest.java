package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void getAllUsers_callsFindAll_andMapsToDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "u1@mail.com", "U1"),
                new User(2L, "u2@mail.com", "U2")
        ));

        List<UserDto> result = userService.getAllUsers();

        verify(userRepository).findAll();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getEmail()).isEqualTo("u1@mail.com");
    }

    @Test
    void saveUser_savesUser_andReturnsDto() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "existing@mail.com", "Existing")
        ));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UserDto result = userService.saveUser(new UserDto(null, "new@mail.com", "New"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@mail.com");
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void getUserById_callsFindById_andReturnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "u@mail.com", "User")));

        UserDto result = userService.getUserById(1L);

        verify(userRepository).findById(1L);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("u@mail.com");
    }

    @Test
    void updateUser_updatesFields_andDoesNotSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "old@mail.com", "Old")));
        when(userRepository.findAll()).thenReturn(List.of(new User(2L, "other@mail.com", "Other")));

        UserDto result = userService.updateUser(1L, new UserDto(null, "new@mail.com", "NewName"));

        verify(userRepository).findById(1L);
        verify(userRepository).findAll();
        verify(userRepository, never()).save(any(User.class));
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
        assertThat(result.getName()).isEqualTo("NewName");
    }

    @Test
    void deleteUser_callsDeleteById() {
        userService.deleteuser(1L);
        verify(userRepository).deleteById(1L);
    }
}

