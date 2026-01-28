package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    RequestServiceImpl requestService;

    @Test
    void addRequest_savesRequest_andReturnsDto() {
        User requestor = new User(1L, "u@mail.com", "User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(requestRepository.save(any(ItemRequest.class))).thenAnswer(inv -> {
            ItemRequest r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        RequestDto dto = new RequestDto();
        dto.setDescription("Need item");

        ResponseDto result = requestService.addRequest(dto, 1L);

        verify(userRepository).findById(1L);
        verify(requestRepository).save(any(ItemRequest.class));
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getDescription()).isEqualTo("Need item");
        assertThat(result.getRequester().getId()).isEqualTo(1L);
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void getRequests_loadsByRequestor_andReturnsSortedDtos() {
        User requestor = new User(1L, "u@mail.com", "User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        ItemRequest older = new ItemRequest();
        older.setId(1L);
        older.setDescription("old");
        older.setRequestor(requestor);
        older.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));

        ItemRequest newer = new ItemRequest();
        newer.setId(2L);
        newer.setDescription("new");
        newer.setRequestor(requestor);
        newer.setCreated(LocalDateTime.of(2026, 1, 2, 10, 0));

        when(requestRepository.findAllRequestByRequestorId(1L)).thenReturn(List.of(older, newer));

        List<ResponseDto> result = requestService.getRequests(1L);

        verify(userRepository).findById(1L);
        verify(requestRepository).findAllRequestByRequestorId(1L);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(2L); // sorted desc by created
        assertThat(result.get(1).getId()).isEqualTo(1L);
    }

    @Test
    void getAllRequests_returnsSortedDtos() {
        User requestor = new User(1L, "u@mail.com", "User");

        ItemRequest older = new ItemRequest();
        older.setId(1L);
        older.setDescription("old");
        older.setRequestor(requestor);
        older.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));

        ItemRequest newer = new ItemRequest();
        newer.setId(2L);
        newer.setDescription("new");
        newer.setRequestor(requestor);
        newer.setCreated(LocalDateTime.of(2026, 1, 2, 10, 0));

        when(requestRepository.findAll()).thenReturn(List.of(older, newer));

        List<ResponseDto> result = requestService.getAllRequests();

        verify(requestRepository).findAll();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(2L);
    }

    @Test
    void getRequestById_findsById_andReturnsDto() {
        User requestor = new User(1L, "u@mail.com", "User");
        ItemRequest req = new ItemRequest();
        req.setId(5L);
        req.setDescription("by id");
        req.setRequestor(requestor);
        req.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(requestRepository.findById(5L)).thenReturn(Optional.of(req));

        ResponseDto result = requestService.getRequestById(5L);

        verify(requestRepository).findById(5L);
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getDescription()).isEqualTo("by id");
    }
}

