package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest sendRequest(long userId, String text) {
        return null;
    }

    @Override
    public BookingDto addBooking(BookingDto bookingDto) {
        Booking booking = bookingMapper.createBooking(bookingDto);
        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new InvalidAccessException("Не указана дата начала или дата окончания бронивание вещи.");
        }
        bookingRepository.save(booking);
        return bookingMapper.createBookingDto(booking);
    }

    @Override
    public String acceptBooking(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (userId != item.getOwner().getId()) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на подтверждении бронирования предмета с id " + itemId);
        }
        Booking booking = bookingRepository.findBookingByItemAndUserId(userId, item).orElseThrow(() -> new NotFoundException("Для пользователя с id " + userId + "не найдено бронирование предмета с id " + itemId));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        return "Бронирование успешно подтверждено";
    }

}
