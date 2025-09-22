package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private Long request;
    private String description;
    private Boolean available;
    private User owner;

    public ItemDto(String description, String name, Boolean available, Long request) {
        this.description = description;
        this.name = name;
        this.available = available;
        this.request = request;
    }

    public ItemDto(String description, String name, Boolean available) {
        this.description = description;
        this.name = name;
        this.available = available;
    }

    public ItemDto() {
    }
}
