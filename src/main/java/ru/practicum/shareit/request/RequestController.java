package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public class RequestController {
    private RequestService requestService;

    public ItemRequest sendRequest(@RequestHeader("X-Later-User-Id") long userId,
                                   @PathVariable String text) {
        return requestService.sendRequest(userId, text);
    }
}
