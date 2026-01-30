package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class ItemPotentialDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
}
