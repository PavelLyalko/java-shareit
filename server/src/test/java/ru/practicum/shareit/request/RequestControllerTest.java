package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RequestService requestService;

    @Test
    void addRequest_returnsResponseDto() throws Exception {
        ResponseDto response = new ResponseDto(
                1L,
                "Need item",
                new UserDto(1L, "user@mail.com", "User"),
                LocalDateTime.of(2026, 1, 1, 10, 0),
                List.of()
        );
        when(requestService.addRequest(any(RequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need item\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need item"));
    }

    @Test
    void getRequests_returnsList() throws Exception {
        ResponseDto response = new ResponseDto();
        response.setId(1L);
        response.setDescription("Need item");
        when(requestService.getRequests(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllRequests_returnsList() throws Exception {
        ResponseDto response = new ResponseDto();
        response.setId(2L);
        response.setDescription("Other");
        when(requestService.getAllRequests()).thenReturn(List.of(response));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void getRequestById_returnsResponseDto() throws Exception {
        ResponseDto response = new ResponseDto();
        response.setId(3L);
        response.setDescription("By id");
        when(requestService.getRequestById(3L)).thenReturn(response);

        mockMvc.perform(get("/requests/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("By id"));
    }
}

