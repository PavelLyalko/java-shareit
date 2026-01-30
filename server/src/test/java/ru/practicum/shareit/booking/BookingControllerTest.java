package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @Test
    void createBooking_mapsRequestAndHeader_andReturnsBooking() throws Exception {
        BookingDto response = new BookingDto(
                1L,
                LocalDateTime.of(2026, 1, 1, 10, 0, 0),
                LocalDateTime.of(2026, 1, 2, 10, 0, 0),
                new ItemDto(10L, "Drill", "Good drill", true, 0L),
                new UserDto(5L, "booker@mail.com", "Booker"),
                BookingStatus.WAITING
        );
        when(bookingService.createBooking(any(CreateBookingDto.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"2026-01-01T10:00:00\",\"end\":\"2026-01-02T10:00:00\",\"itemId\":10}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(10))
                .andExpect(jsonPath("$.status").value("WAITING"));

        ArgumentCaptor<CreateBookingDto> captor = ArgumentCaptor.forClass(CreateBookingDto.class);
        verify(bookingService).createBooking(captor.capture());
        assertThat(captor.getValue().getBookerId()).isEqualTo(5L);
        assertThat(captor.getValue().getItemId()).isEqualTo(10L);
        assertThat(captor.getValue().getStart()).isEqualTo(LocalDateTime.of(2026, 1, 1, 10, 0, 0));
        assertThat(captor.getValue().getEnd()).isEqualTo(LocalDateTime.of(2026, 1, 2, 10, 0, 0));
    }

    @Test
    void acceptBooking_returnsBooking() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(2L);
        response.setStatus(BookingStatus.APPROVED);
        when(bookingService.acceptBooking(1L, true, 2L)).thenReturn(response);

        mockMvc.perform(patch("/bookings/2")
                        .header(USER_HEADER, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_returnsBooking() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(3L);
        when(bookingService.getBooking(1L, 3L)).thenReturn(response);

        mockMvc.perform(get("/bookings/3")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void getBookingsByUser_returnsList() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(4L);
        when(bookingService.getBookingsByUser(1L, BookingState.ALL)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    void getBookingsByOwner_returnsList() throws Exception {
        BookingDto booking = new BookingDto();
        booking.setId(5L);
        when(bookingService.getBookingsByOwner(1L, BookingState.ALL)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(5));
    }
}

