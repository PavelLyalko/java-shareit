package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

public class ItemRequestMapper {
    public static ResponseDto toResponseDto(ItemRequest itemRequest) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setId(itemRequest.getId());
        responseDto.setDescription(itemRequest.getDescription());
        responseDto.setCreated(itemRequest.getCreated());
        responseDto.setRequester(UserMapper.toUserDto(itemRequest.getRequestor()));
        return responseDto;
    }
}
