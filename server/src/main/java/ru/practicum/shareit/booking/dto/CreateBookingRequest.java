package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private long bookerId;
}
