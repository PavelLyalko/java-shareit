package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

@RequestMapping("/booking")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.addBooking(bookingDto));
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<String> acceptBooking(@RequestHeader("X-Later-User-Id") long userId,
                                                @PathVariable long itemId) {
        return ResponseEntity.ok(bookingService.acceptBooking(userId, itemId));
    }
}
