package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
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
public class RequestServiceImplIntegrationTest {
    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addRequest_returnsResponseDto() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужна дрель");

        ResponseDto result = requestService.addRequest(requestDto, user.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void addRequest_withInvalidUserId_throwsException() {
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Нужна дрель");

        assertThrows(NotFoundException.class, () -> requestService.addRequest(requestDto, 999L));
    }

    @Test
    void getRequests_returnsResponseDtos() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Нужна дрель");
        request1.setRequestor(user);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Нужна пила");
        request2.setRequestor(user);
        request2.setCreated(LocalDateTime.now().minusDays(2));
        requestRepository.save(request2);

        List<ResponseDto> result = requestService.getRequests(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель"); // Самый новый запрос должен быть первым
        assertThat(result.get(1).getDescription()).isEqualTo("Нужна пила");
    }

    @Test
    void getRequests_withInvalidUserId_throwsException() {
        assertThrows(NotFoundException.class, () -> requestService.getRequests(999L));
    }

    @Test
    void getAllRequests_returnsResponseDtos() {
        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setName("User1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setName("User2");
        userRepository.save(user2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Нужна дрель");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        requestRepository.save(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Нужна пила");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now().minusDays(2));
        requestRepository.save(request2);

        List<ResponseDto> result = requestService.getAllRequests();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель"); // Самый новый запрос должен быть первым
        assertThat(result.get(1).getDescription()).isEqualTo("Нужна пила");
    }

    @Test
    void getRequestById_returnsResponseDto() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        ItemRequest request = new ItemRequest();
        request.setDescription("Нужна дрель");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        requestRepository.save(request);

        ResponseDto result = requestService.getRequestById(request.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void getRequestById_withInvalidRequestId_throwsException() {
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(999L));
    }
}
