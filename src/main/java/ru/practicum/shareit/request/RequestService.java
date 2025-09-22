package ru.practicum.shareit.request;

public interface RequestService {
    ItemRequest sendRequest(long userId, String text);
}
