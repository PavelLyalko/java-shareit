package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingDto {
    LocalDateTime start;
    LocalDateTime end;
    long itemId;
    long bookerId;
}
