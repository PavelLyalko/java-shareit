package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
         return repository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        List<UserDto> users = getAllUsers();
        User user = UserMapper.toUser(userDto);
        validationEmail(user, users);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = repository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto user = getUserById(userId);
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty() && validationEmail(UserMapper.toUser(userDto), getAllUsers())) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    @Override
    public void deleteuser(Long userId) {
        repository.deleteUserById(userId);
    }

    private boolean validationEmail(User user, List<UserDto> users) {
        Matcher matcher = EMAIL_PATTERN.matcher(user.getEmail());
        if (!matcher.matches()) {
            throw new InvalidDataException("Неверный формат email");
        }
        users.stream()
                .filter(person -> person.getEmail().equals(user.getEmail()))
                .findAny()
                .ifPresent(person -> {
                    throw new InvalidDataException("Пользователь с email " + user.getEmail() + " уже существует");
                });
        return true;
    }
}