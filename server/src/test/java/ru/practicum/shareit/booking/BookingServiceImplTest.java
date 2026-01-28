package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void createBooking_savesBooking_andReturnsDto() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        User booker = new User(2L, "b@mail.com", "Booker");
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(100L);
            return b;
        });

        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(10L);
        dto.setBookerId(2L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        BookingDto result = bookingService.createBooking(dto);

        verify(itemRepository).findById(10L);
        verify(userRepository).findById(2L);
        verify(bookingRepository).save(any(Booking.class));
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getItem().getId()).isEqualTo(10L);
        assertThat(result.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void acceptBooking_updatesStatus_saves_andReturnsDto() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(5L);
        booking.setItem(item);
        booking.setBooker(new User(2L, "b@mail.com", "Booker"));
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingDto result = bookingService.acceptBooking(1L, true, 5L);

        verify(bookingRepository).findById(5L);
        verify(bookingRepository).save(booking);
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBooking_findsById_andReturnsDto() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(10L);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(7L);
        booking.setItem(item);
        booking.setBooker(new User(2L, "b@mail.com", "Booker"));
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        when(bookingRepository.findById(7L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(2L, 7L);

        verify(bookingRepository).findById(7L);
        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getItem().getId()).isEqualTo(10L);
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED"})
    void getBookingsByUser_callsExpectedRepositoryMethod(BookingState state) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(10L, "Item", null, List.of(), "Desc", true, new User(2L, "o@mail.com", "Owner")));
        booking.setBooker(new User(1L, "b@mail.com", "Booker"));
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        if (state == BookingState.ALL) {
            when(bookingRepository.findAllBookingsByBookerId(1L)).thenReturn(List.of(booking));
        } else if (state == BookingState.CURRENT) {
            when(bookingRepository.findAllByBookerIdAndCurrentBookings(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.FUTURE) {
            when(bookingRepository.findAllByBookerIdAndStartAfter(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.PAST) {
            when(bookingRepository.findAllByBookerIdAndEndBefore(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.WAITING) {
            when(bookingRepository.findAllByBookerIdAndStatus(1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        } else if (state == BookingState.REJECTED) {
            when(bookingRepository.findAllByBookerIdAndStatus(1L, BookingStatus.REJECTED)).thenReturn(List.of(booking));
        }

        List<BookingDto> result = bookingService.getBookingsByUser(1L, state);

        assertThat(result).hasSize(1);
        if (state == BookingState.ALL) {
            verify(bookingRepository).findAllBookingsByBookerId(1L);
        } else if (state == BookingState.CURRENT) {
            verify(bookingRepository).findAllByBookerIdAndCurrentBookings(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.FUTURE) {
            verify(bookingRepository).findAllByBookerIdAndStartAfter(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.PAST) {
            verify(bookingRepository).findAllByBookerIdAndEndBefore(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.WAITING) {
            verify(bookingRepository).findAllByBookerIdAndStatus(1L, BookingStatus.WAITING);
        } else if (state == BookingState.REJECTED) {
            verify(bookingRepository).findAllByBookerIdAndStatus(1L, BookingStatus.REJECTED);
        }
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED"})
    void getBookingsByOwner_callsExpectedRepositoryMethod(BookingState state) {
        when(userRepository.existsById(1L)).thenReturn(true);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item(10L, "Item", null, List.of(), "Desc", true, new User(1L, "o@mail.com", "Owner")));
        booking.setBooker(new User(2L, "b@mail.com", "Booker"));
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        if (state == BookingState.ALL) {
            when(bookingRepository.findAllByItemOwnerId(1L)).thenReturn(List.of(booking));
        } else if (state == BookingState.CURRENT) {
            when(bookingRepository.findAllByItemOwnerIdAndCurrentBookings(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.FUTURE) {
            when(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.PAST) {
            when(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(booking));
        } else if (state == BookingState.WAITING) {
            when(bookingRepository.findAllByItemOwnerIdAndStatus(1L, BookingStatus.WAITING)).thenReturn(List.of(booking));
        } else if (state == BookingState.REJECTED) {
            when(bookingRepository.findAllByItemOwnerIdAndStatus(1L, BookingStatus.REJECTED)).thenReturn(List.of(booking));
        }

        List<BookingDto> result = bookingService.getBookingsByOwner(1L, state);

        verify(userRepository).existsById(1L);
        assertThat(result).hasSize(1);
        if (state == BookingState.ALL) {
            verify(bookingRepository).findAllByItemOwnerId(1L);
        } else if (state == BookingState.CURRENT) {
            verify(bookingRepository).findAllByItemOwnerIdAndCurrentBookings(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.FUTURE) {
            verify(bookingRepository).findAllByItemOwnerIdAndStartAfter(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.PAST) {
            verify(bookingRepository).findAllByItemOwnerIdAndEndBefore(eq(1L), any(LocalDateTime.class));
        } else if (state == BookingState.WAITING) {
            verify(bookingRepository).findAllByItemOwnerIdAndStatus(1L, BookingStatus.WAITING);
        } else if (state == BookingState.REJECTED) {
            verify(bookingRepository).findAllByItemOwnerIdAndStatus(1L, BookingStatus.REJECTED);
        }
    }
}

