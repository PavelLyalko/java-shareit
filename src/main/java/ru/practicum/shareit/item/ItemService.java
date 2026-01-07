package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    Item addNewItem(Long userId, ItemDto itemDto) throws NotFoundException;

    void deleteItem(long userId, long itemId);

    List<ItemResponse> getItems(long userId);

    Item editItem(long userId, ItemDto itemDto, long itemId);

    Item getItem(long userId, long itemId);

    List<Item> potentialItems(String text, long userId);

    void addComment(CommentDto commentDto);
}
