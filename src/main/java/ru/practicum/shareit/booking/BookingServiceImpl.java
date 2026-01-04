package ru.practicum.shareit.booking;


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
import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto) {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(createBookingDto.getStart());
        booking.setEnd(createBookingDto.getEnd());

        User booker = new User();
        booker.setId(createBookingDto.getBookerId());
        booking.setBooker(booker);
        Item bookingItem = new Item();
        bookingItem.setId(createBookingDto.getItemId());
        booking.setItem(bookingItem);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.createBookingDto(savedBooking);
    }

    @Override
    public void acceptBooking(long userId, boolean approved, long bookingId) {
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
    }

    @Override
    public BookingDto getBooking(long userId ,long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new InvalidAccessException("Пользователь с id: " + userId + " не имеет прав на получение данных о бронировании id " + bookingId);
        }
        return bookingMapper.createBookingDto(booking);
    }

    @Override
    public List<Booking> getBookingsByUser(long userId, BookingState state) {
        if (state == BookingState.ALL) {
            return bookingRepository.findAllBookingsByBookerId(userId);
        }
        if  (state == BookingState.CURRENT) {
            return bookingRepository.findAllByBookerIdAndCurrentBookings(userId, LocalDateTime.now());
        }
        if (state == BookingState.FUTURE) {
            return bookingRepository.findAllByBookerIdAndStartAfter(userId, LocalDateTime.now());
        }
        if (state == BookingState.PAST) {
            return bookingRepository.findAllByBookerIdAndEndBefore(userId,LocalDateTime.now());
        }
        if (state == BookingState.WAITING) {
            return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
        }
        if (state == BookingState.REJECTED) {
            return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
        }

        return List.of();
    }

    @Override
    public List<Booking> getBookingsByOwner(long userId, BookingState state) {
        if (state == BookingState.ALL) {
            return bookingRepository.findAllByItemOwnerId(userId);
        }
        if (state == BookingState.CURRENT) {
            return bookingRepository.findAllByItemOwnerIdAndCurrentBookings(userId, LocalDateTime.now());
        }
        if (state == BookingState.FUTURE) {
            return bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, LocalDateTime.now());
        }
        if (state == BookingState.PAST) {
            return bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, LocalDateTime.now());
        }
        if (state == BookingState.WAITING) {
            return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
        }
        if (state == BookingState.REJECTED) {
            return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
        }
        return List.of();
    }
}
