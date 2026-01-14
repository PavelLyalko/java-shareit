package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto) throws NotFoundException;

    void deleteItem(long userId, long itemId);

    List<ItemResponse> getItems(long userId);

    ItemDto editItem(long userId, ItemDto itemDto, long itemId);

    ItemCommentsResponse getItem(long userId, long itemId);

    List<ItemPotentialDto> potentialItems(String text, long userId);

    CommentResponse addComment(CommentDto commentDto);
}
