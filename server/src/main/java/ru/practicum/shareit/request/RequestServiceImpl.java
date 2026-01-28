package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ResponseDto addRequest(RequestDto requestDto, long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("не найден пользователь с id: " + requestorId));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        return ItemRequestMapper.toResponseDto(requestRepository.save(itemRequest));
    }

    @Transactional
    @Override
    public List<ResponseDto> getRequests(long requestorId) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("не найден пользователь с id: " + requestorId));
        List<ItemRequest> itemRequests = requestRepository.findAllRequestByRequestorId(requestor.getId());
        return itemRequests.stream().map(ItemRequestMapper::toResponseDto).sorted(Comparator.comparing(ResponseDto::getCreated).reversed()).toList();
    }

    @Override
    public List<ResponseDto> getAllRequests() {
        return requestRepository.findAll().stream().map(ItemRequestMapper::toResponseDto).sorted(Comparator.comparing(ResponseDto::getCreated).reversed()).toList();
    }

    @Override
    public ResponseDto getRequestById(long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с id: " + requestId + " не найден"));
        return ItemRequestMapper.toResponseDto(itemRequest);
    }
}
