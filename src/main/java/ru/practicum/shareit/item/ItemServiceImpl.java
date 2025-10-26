package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.InvalidFieldException;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        if (itemDto.getAvailable() == null) {
            throw new InvalidFieldException("Поле available не может быть пустым.");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new InvalidFieldException("Поле name не может быть null или пустым.");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new InvalidFieldException("Поле description не может быть null или пустым.");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return item;
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (item.getOwner().getId() != userId) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на удаление предмета с id " + itemId);
        }
        itemRepository.deleteItem(item);
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Item editItem(long userId, ItemDto itemDto, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (userId != item.getOwner().getId()) {
            throw new InvalidUserException("Пользователь с id " + userId + " не имеет прав на редактирование предмета с id " + itemId);
        }
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }
        if (!item.getAvailable().equals(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return item;
    }

    @Override
    public Item getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (userId != item.getOwner().getId()) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на получение предмета с id " + itemId);
        }
        return item;
    }

    @Override
    public List<Item> potentialItems(String text, long userId) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.potentialItems(text);
    }
}
