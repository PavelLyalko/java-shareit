package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Value;

@Data
public class CommentDto {
    private String text;
    private long itemId;
    private long userId;
}
