package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseDto addRequest(@RequestHeader(X_SHARER_USER_ID_HEADER) long requestorId,
                                  @RequestBody RequestDto requestDto) {
        return requestService.addRequest(requestDto, requestorId);
    }

    @GetMapping
    public List<ResponseDto> getRequests(@RequestHeader(X_SHARER_USER_ID_HEADER) long requestorId) {
        return requestService.getRequests(requestorId);
    }

    @GetMapping("/all")
    public List<ResponseDto> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseDto getRequestById(@PathVariable long requestId) {
        return requestService.getRequestById(requestId);
    }
}
