package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
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
        itemRepository.delete(item);
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findAllByOwnerId(userId);
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
        return null; //itemRepository.potentialItems(text);
    }
}
