package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    public Booking createBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setBooker(bookingDto.getBooker());
        booking.setStatus(bookingDto.getStatus());
        booking.setItem(bookingDto.getItem());
        return booking;
    }

    public BookingDto createBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(booking.getItem());
        return bookingDto;
    }
}
