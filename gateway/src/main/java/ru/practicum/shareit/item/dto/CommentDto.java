package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDto {
    @NotBlank(message = "Текст не должен быть пустым")
    private String text;
    private long itemId;
    private long userId;
}
