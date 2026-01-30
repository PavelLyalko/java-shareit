package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createBooking_returnsBookingDto() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(item.getId());
        createBookingDto.setBookerId(booker.getId());
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createBooking(createBookingDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getStart()).isEqualTo(createBookingDto.getStart());
        assertThat(result.getEnd()).isEqualTo(createBookingDto.getEnd());
    }

    @Test
    void createBooking_withUnavailableItem_throwsException() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(false);
        item.setOwner(owner);
        itemRepository.save(item);

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(item.getId());
        createBookingDto.setBookerId(booker.getId());
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(InvalidAccessException.class, () -> bookingService.createBooking(createBookingDto));
    }

    @Test
    void createBooking_withOwnerAsBooker_throwsException() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(item.getId());
        createBookingDto.setBookerId(owner.getId());
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(InvalidAccessException.class, () -> bookingService.createBooking(createBookingDto));
    }

    @Test
    void acceptBooking_returnsBookingDto() {

        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        BookingDto result = bookingService.acceptBooking(owner.getId(), true, booking.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void acceptBooking_withInvalidUser_throwsException() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        assertThrows(InvalidAccessException.class, () -> bookingService.acceptBooking(999L, true, booking.getId()));
    }

    @Test
    void acceptBooking_withInvalidStatus_throwsException() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        assertThrows(InvalidAccessException.class, () -> bookingService.acceptBooking(owner.getId(), true, booking.getId()));
    }

    @Test
    void getBooking_returnsBookingDto() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        BookingDto result = bookingService.getBooking(booker.getId(), booking.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBooking_withInvalidUser_throwsException() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        assertThrows(InvalidAccessException.class, () -> bookingService.getBooking(999L, booking.getId()));
    }

    @Test
    void getBookingsByUser_returnsBookingDtos() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        booking2.setBooker(booker);
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), BookingState.ALL);

        assertThat(result).hasSize(2);
    }

    @Test
    void getBookingsByOwner_returnsBookingDtos() {
        User owner = new User();
        owner.setEmail("owner@mail.com");
        owner.setName("Owner");
        userRepository.save(owner);

        User booker = new User();
        booker.setEmail("booker@mail.com");
        booker.setName("Booker");
        userRepository.save(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setBooker(booker);
        booking1.setItem(item);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        booking2.setBooker(booker);
        booking2.setItem(item);
        bookingRepository.save(booking2);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL);

        assertThat(result).hasSize(2);
    }
}
