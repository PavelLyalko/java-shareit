package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.ItemRequest;

public interface BookingService {
    ItemRequest sendRequest(long userId, String text);

    BookingDto addBooking(BookingDto bookingDto);

    String acceptBooking(long userId, long itemId);
}

