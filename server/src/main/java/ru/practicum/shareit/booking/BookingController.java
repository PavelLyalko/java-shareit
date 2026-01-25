package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.List;

@RequestMapping("/bookings")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID_VALUE = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(X_SHARER_USER_ID_VALUE) long userId,
                                    @RequestBody CreateBookingRequest request) {
        CreateBookingDto createBookingDto = BookingDtoMapper.toCreateBookingDto(request);
        createBookingDto.setBookerId(userId);
        BookingDto bookingDto = bookingService.createBooking(createBookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto acceptBooking(@RequestHeader(X_SHARER_USER_ID_VALUE) long userId,
                                    @RequestParam(name = "approved", required = false) boolean approved,
                                    @PathVariable long bookingId) {
        return bookingService.acceptBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(X_SHARER_USER_ID_VALUE) long userId,
                                 @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader(X_SHARER_USER_ID_VALUE) long userId,
                                              @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByUser(userId, state); //TODO ДОБАВИТЬ СТРАНИЧНУЮ ПАГИНАЦИЮ
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(X_SHARER_USER_ID_VALUE) long userId,
                                               @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}

