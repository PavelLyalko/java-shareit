package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findByUserId(long userId);

    Item save(Item item);

    void deleteItem(Item item);

    Optional<Item> findById(long itemId);

    List<Item> potentialItems(String text);
}