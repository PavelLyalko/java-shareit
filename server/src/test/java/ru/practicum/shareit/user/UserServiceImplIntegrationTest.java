package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.DataValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getAllUsers_returnsAllUsers() {
         User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setName("User1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setName("User2");
        userRepository.save(user2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@mail.com");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@mail.com");
    }

    @Test
    void saveUser_savesUserAndReturnsDto() {
        UserDto userDto = new UserDto(null, "new@mail.com", "New");

        UserDto savedUser = userService.saveUser(userDto);
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("new@mail.com");
        assertThat(savedUser.getName()).isEqualTo("New");
    }

    @Test
    void saveUser_withInvalidEmail_throwsException() {
        UserDto userDto = new UserDto(null, "invalid-email", "New");

        assertThrows(DataValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void saveUser_withDuplicateEmail_throwsException() {
        User user = new User();
        user.setEmail("duplicate@mail.com");
        user.setName("User");
        userRepository.save(user);

        UserDto userDto = new UserDto(null, "duplicate@mail.com", "New");

        assertThrows(DataValidationException.class, () -> userService.saveUser(userDto));
    }

    @Test
    void getUserById_returnsUser() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        UserDto userDto = userService.getUserById(user.getId());

        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getEmail()).isEqualTo("user@mail.com");
        assertThat(userDto.getName()).isEqualTo("User");
    }

    @Test
    void getUserById_withInvalidId_throwsException() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_updatesUserAndReturnsDto() {
        User user = new User();
        user.setEmail("old@mail.com");
        user.setName("Old");
        userRepository.save(user);

        UserDto updateDto = new UserDto(null, "new@mail.com", "New");

        UserDto updatedUser = userService.updateUser(user.getId(), updateDto);

        assertThat(updatedUser.getId()).isEqualTo(user.getId());
        assertThat(updatedUser.getEmail()).isEqualTo("new@mail.com");
        assertThat(updatedUser.getName()).isEqualTo("New");
    }

    @Test
    void updateUser_withInvalidEmail_throwsException() {
        User user = new User();
        user.setEmail("old@mail.com");
        user.setName("Old");
        userRepository.save(user);

        UserDto updateDto = new UserDto(null, "invalid-email", "New");

        assertThrows(DataValidationException.class, () -> userService.updateUser(user.getId(), updateDto));
    }


    @Test
    void deleteUser_deletesUser() {
        User user = new User();
        user.setEmail("toDelete@mail.com");
        user.setName("ToDelete");
        userRepository.save(user);

        userService.deleteuser(user.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }
}
