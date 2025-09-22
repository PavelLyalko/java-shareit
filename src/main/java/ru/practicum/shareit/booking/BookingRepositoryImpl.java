package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private List<Booking> bookings = new ArrayList<>();

    @Override
    public Booking save(Booking booking) {
        booking.setId(getId());
        bookings.add(booking);
        return booking;
    }

    @Override
    public Optional<Booking> findBookingByItemAndUserId(long userId, Item item) {
        return bookings.stream()
                .filter(booking -> booking.getItem().equals(item))
                .filter(booking -> booking.getBooker().getId() == userId)
                .findFirst();
    }

    private long getId() {
        long lastId = bookings.stream()
                .mapToLong(Booking::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
