package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto createBookingDto);

    void acceptBooking(long userId, boolean approved, long bookingId);

    BookingDto getBooking(long userId ,long bookingId);

    List<Booking> getBookingsByUser(long userId, BookingState state);

    List<Booking> getBookingsByOwner(long userId, BookingState state);
}