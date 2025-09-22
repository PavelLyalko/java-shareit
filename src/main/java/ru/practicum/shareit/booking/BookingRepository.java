package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findBookingByItemAndUserId(long userId, Item item);
}
