package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto) {
        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));

        if (!item.getAvailable()) {
            throw new InvalidAccessException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId().equals(createBookingDto.getBookerId())) {
            throw new InvalidAccessException("Владелец не может забронировать свою вещь");
        }

        User user = userRepository.findById(createBookingDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id " + createBookingDto.getBookerId() + " не найден"));

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.createBookingDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDto acceptBooking(long userId, boolean approved, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        long ownerId = booking.getItem().getOwner().getId();
        if (userId != ownerId) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на подтверждение бронирования предмета с id " + ownerId);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidAccessException("Бронирование не находится в статусе ожидания и не может быть подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);

        return BookingMapper.createBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new InvalidAccessException("Пользователь с id: " + userId + " не имеет прав на получение данных о бронировании id " + bookingId);
        }
        return BookingMapper.createBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(long userId, BookingState state) {
        List<Booking> bookings = new ArrayList<>();
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findAllBookingsByBookerId(userId);
        }
        if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findAllByBookerIdAndCurrentBookings(userId, LocalDateTime.now());
        }
        if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
        }
        if (state == BookingState.PAST) {
            bookings = bookingRepository.findAllByBookerIdAndEndBefore(userId, LocalDateTime.now());
        }
        if (state == BookingState.WAITING) {
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
        }
        if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        }

        return bookings.stream()
                .map(BookingMapper::createBookingDto)
                .toList();
    }

    @Transactional
    @Override
    public List<BookingDto> getBookingsByOwner(long userId, BookingState state) {

        List<Booking> bookings = new ArrayList<>();

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Wrong user id");
        }

        if (state == BookingState.ALL) {
            bookings = bookingRepository.findAllByItemOwnerId(userId);
        }
        if (state == BookingState.CURRENT) {
            bookings = bookingRepository.findAllByItemOwnerIdAndCurrentBookings(userId, LocalDateTime.now());
        }
        if (state == BookingState.FUTURE) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now());
        }
        if (state == BookingState.PAST) {
            bookings = bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now());
        }
        if (state == BookingState.WAITING) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
        }
        if (state == BookingState.REJECTED) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
        }
        return bookings.stream()
                .map(BookingMapper::createBookingDto)
                .toList();
    }
}
