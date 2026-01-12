package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private LocalDateTime lastBookingStartDate;
    private LocalDateTime lastBookingEndDate;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private LocalDateTime nextBookingStartDate;
    private LocalDateTime nextBookingEndDate;
}
