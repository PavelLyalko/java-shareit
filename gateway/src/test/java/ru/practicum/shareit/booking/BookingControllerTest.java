package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    BookingClient bookingClient;

    @Test
    void getBookingsByUser_delegatesToClient() throws Exception {
        when(bookingClient.getBookings(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1))));

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 10);
    }

    @Test
    void createBooking_delegatesToClient() throws Exception {
        when(bookingClient.bookItem(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("id", 10)));

        mockMvc.perform(post("/bookings")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":10,\"start\":\"2030-01-01T10:00:00\",\"end\":\"2030-01-02T10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10));

        verify(bookingClient).bookItem(eq(1L), any());
    }

    @Test
    void getBooking_delegatesToClient() throws Exception {
        when(bookingClient.getBooking(1L, 5L)).thenReturn(ResponseEntity.ok(Map.of("id", 5)));

        mockMvc.perform(get("/bookings/5")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5));

        verify(bookingClient).getBooking(1L, 5L);
    }

    @Test
    void acceptBooking_delegatesToClient() throws Exception {
        when(bookingClient.acceptBooking(1L, true, 5L)).thenReturn(ResponseEntity.ok(Map.of("id", 5, "approved", true)));

        mockMvc.perform(patch("/bookings/5")
                        .header(USER_HEADER, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5));

        verify(bookingClient).acceptBooking(1L, true, 5L);
    }

    @Test
    void getBookingsByOwner_delegatesToClient() throws Exception {
        when(bookingClient.getBookingsByOwner(1L, BookingState.ALL))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", 2))));

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2));

        verify(bookingClient).getBookingsByOwner(1L, BookingState.ALL);
    }
}

