package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@RequestMapping("/bookings")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String X_LATER_USER_ID_VALUE = "X-Later-User-Id";

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingRequest request) {
        CreateBookingDto createBookingDto = BookingDtoMapper.toCreateBookingDto(request);
        return ResponseEntity.ok(bookingService.createBooking(createBookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Void> acceptBooking(@RequestHeader(X_LATER_USER_ID_VALUE) long userId,
                                              @RequestParam("approved") boolean approved,
                                              @PathVariable long bookingId) {
        bookingService.acceptBooking(userId, approved, bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(X_LATER_USER_ID_VALUE) long userId,
                                 @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingsByUser(@RequestHeader(X_LATER_USER_ID_VALUE) long userId,
                                           @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner(@RequestHeader(X_LATER_USER_ID_VALUE) long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}

