package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    RequestClient requestClient;

    @Test
    void addRequest_delegatesToClient() throws Exception {
        when(requestClient.addRequest(any(), eq(1L))).thenReturn(ResponseEntity.ok(Map.of("id", 1, "description", "Need item")));

        mockMvc.perform(post("/requests")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need item\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need item"));

        verify(requestClient).addRequest(any(), eq(1L));
    }

    @Test
    void getRequests_delegatesToClient() throws Exception {
        when(requestClient.getRequests(1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1))));

        mockMvc.perform(get("/requests")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(requestClient).getRequests(1L);
    }

    @Test
    void getAllRequests_delegatesToClient() throws Exception {
        when(requestClient.getAllRequests()).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 2))));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2));

        verify(requestClient).getAllRequests();
    }

    @Test
    void getRequestById_delegatesToClient() throws Exception {
        when(requestClient.getRequestById(3L)).thenReturn(ResponseEntity.ok(Map.of("id", 3)));

        mockMvc.perform(get("/requests/3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3));

        verify(requestClient).getRequestById(3L);
    }
}

