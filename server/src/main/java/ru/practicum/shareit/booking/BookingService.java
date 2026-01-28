package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto createBookingDto);

    BookingDto acceptBooking(long userId, boolean approved, long bookingId);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getBookingsByUser(long userId, BookingState state);

    List<BookingDto> getBookingsByOwner(long userId, BookingState state);
}