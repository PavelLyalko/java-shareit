package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private LocalDateTime lastBookingStartDate;
    private LocalDateTime lastBookingEndDate;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private LocalDateTime nextBookingStartDate;
    private LocalDateTime nextBookingEndDate;
}
