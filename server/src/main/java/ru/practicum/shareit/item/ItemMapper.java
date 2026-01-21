package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

@Component
public class ItemMapper {

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setId(item.getId());
        return itemDto;
    }

    public static ItemPotentialDto toItemPotentialDto(Item item) {
        ItemPotentialDto itemPotentialDto = new ItemPotentialDto();
        itemPotentialDto.setId(item.getId());
        itemPotentialDto.setName(item.getName());
        itemPotentialDto.setDescription(item.getDescription());
        itemPotentialDto.setAvailable(item.getAvailable());
        itemPotentialDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        return itemPotentialDto;
    }
}
