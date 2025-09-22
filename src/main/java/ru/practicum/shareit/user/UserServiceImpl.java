package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
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
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) {
        List<User> users = getAllUsers();
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidDataException("Поле email не может  быть пустым.");
        }
        validationEmail(user, users);
        return repository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return repository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    public User updateUser(Long userId, User user) {
        User person = getUserById(userId);
        if (user.getName() != null && !user.getName().isEmpty()) {
            person.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && validationEmail(user, getAllUsers())) {
            person.setEmail(user.getEmail());
        }
        return person;
    }

    @Override
    public void deleteuser(Long userId) {
        List<User> users = getAllUsers();
        if (users.contains(getUserById(userId))) {
            users.remove(userId);
        }
    }

    private boolean validationEmail(User user, List<User> users) {
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