package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    private Long request;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    private Boolean available;
    private User owner;
}
