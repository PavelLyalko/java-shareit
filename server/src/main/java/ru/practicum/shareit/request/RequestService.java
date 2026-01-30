package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.List;

public interface RequestService {

    ResponseDto addRequest(RequestDto requestDto, long requestorId);

    List<ResponseDto> getRequests(long requestorId);

    List<ResponseDto> getAllRequests();

    ResponseDto getRequestById(long requestId);
}
