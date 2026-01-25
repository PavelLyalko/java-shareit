package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemClient itemClient;

    @Test
    void getItems_delegatesToClient() throws Exception {
        when(itemClient.getItems(1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 1))));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient).getItems(1L);
    }

    @Test
    void add_delegatesToClient() throws Exception {
        when(itemClient.addNewItem(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("id", 10)));

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"Good drill\",\"available\":true,\"requestId\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10));

        verify(itemClient).addNewItem(eq(1L), any());
    }

    @Test
    void deleteItem_delegatesToClient() throws Exception {
        when(itemClient.deleteItem(1L, 2L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/2")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk());

        verify(itemClient).deleteItem(1L, 2L);
    }

    @Test
    void editItem_delegatesToClient() throws Exception {
        when(itemClient.editItem(eq(1L), any(), eq(2L))).thenReturn(ResponseEntity.ok(Map.of("id", 2, "name", "New")));

        mockMvc.perform(patch("/items/2")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("New"));

        verify(itemClient).editItem(eq(1L), any(), eq(2L));
    }

    @Test
    void getItem_delegatesToClient() throws Exception {
        when(itemClient.getItem(1L, 2L)).thenReturn(ResponseEntity.ok(Map.of("id", 2)));

        mockMvc.perform(get("/items/2")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2));

        verify(itemClient).getItem(1L, 2L);
    }

    @Test
    void potentialItems_delegatesToClient() throws Exception {
        when(itemClient.potentialItems("sa", 1L)).thenReturn(ResponseEntity.ok(List.of(Map.of("id", 3))));

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, "1")
                        .param("text", "sa"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(3));

        verify(itemClient).potentialItems("sa", 1L);
    }

    @Test
    void addComment_setsIds_andDelegatesToClient() throws Exception {
        when(itemClient.addComment(any())).thenReturn(ResponseEntity.ok(Map.of("id", 100, "text", "Nice item")));

        mockMvc.perform(post("/items/2/comment")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Nice item\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Nice item"));

        ArgumentCaptor<CommentDto> captor = ArgumentCaptor.forClass(CommentDto.class);
        verify(itemClient).addComment(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getItemId()).isEqualTo(2L);
        assertThat(captor.getValue().getText()).isEqualTo("Nice item");
    }
}

