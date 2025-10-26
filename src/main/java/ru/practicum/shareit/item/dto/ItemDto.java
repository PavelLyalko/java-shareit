package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.model.User;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRequest() {
        return request;
    }

    public void setRequest(Long request) {
        this.request = request;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
