package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CreateBookingRequest {
    @NotNull
    LocalDateTime start;
    @NotNull
    LocalDateTime end;
    long itemId;
    long bookerId;
}
