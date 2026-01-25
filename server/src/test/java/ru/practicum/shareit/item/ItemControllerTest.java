package ru.practicum.shareit.item;

import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    ItemService itemService;

    @Test
    void getItems_returnsList() throws Exception {
        ItemResponse item = new ItemResponse();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        when(itemService.getItems(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void add_returnsItemDto() throws Exception {
        ItemDto response = new ItemDto(10L, "Drill", "Good drill", true, 0L);
        when(itemService.addNewItem(eq(1L), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"Good drill\",\"available\":true,\"requestId\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void deleteItem_returnsOk() throws Exception {
        doNothing().when(itemService).deleteItem(1L, 2L);

        mockMvc.perform(delete("/items/2")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L, 2L);
    }

    @Test
    void editItem_returnsItemDto() throws Exception {
        ItemDto response = new ItemDto(2L, "New name", "New desc", true, 0L);
        when(itemService.editItem(eq(1L), any(ItemDto.class), eq(2L))).thenReturn(response);

        mockMvc.perform(patch("/items/2")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New name\",\"description\":\"New desc\",\"available\":true,\"requestId\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("New name"));
    }

    @Test
    void getItem_returnsItemWithComments() throws Exception {
        ItemCommentsResponse response = new ItemCommentsResponse();
        response.setId(2L);
        response.setName("Drill");
        response.setDescription("Good drill");
        response.setAvailable(true);
        response.setComments(List.of());
        when(itemService.getItem(1L, 2L)).thenReturn(response);

        mockMvc.perform(get("/items/2")
                        .header(USER_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void potentialItems_returnsList() throws Exception {
        ItemPotentialDto potential = new ItemPotentialDto();
        potential.setId(3L);
        potential.setName("Saw");
        potential.setDescription("Hand saw");
        potential.setAvailable(true);
        when(itemService.potentialItems("sa", 1L)).thenReturn(List.of(potential));

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, "1")
                        .param("text", "sa"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("Saw"));
    }

    @Test
    void addComment_setsUserAndItemIds_andReturnsResponse() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(100L);
        response.setText("Nice item");
        response.setAuthorName("User");
        response.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(itemService.addComment(any(CommentDto.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header(USER_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Nice item\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.text").value("Nice item"));

        ArgumentCaptor<CommentDto> captor = ArgumentCaptor.forClass(CommentDto.class);
        verify(itemService).addComment(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getItemId()).isEqualTo(2L);
        assertThat(captor.getValue().getText()).isEqualTo("Nice item");
    }
}

