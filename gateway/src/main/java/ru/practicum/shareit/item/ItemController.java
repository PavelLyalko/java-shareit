package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemClient.getItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object>  deleteItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                           @PathVariable(name = "itemId") long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                            @PathVariable long itemId,
                            @RequestBody ItemDto itemDto) {
        return itemClient.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                        @PathVariable(name = "itemId") long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> potentialItems(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                                 @RequestParam("text") String text) {
        return itemClient.potentialItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                      @PathVariable long itemId,
                                      @RequestBody CommentDto commentDto) {
        commentDto.setItemId(itemId);
        commentDto.setUserId(userId);
        return itemClient.addComment(commentDto);
    }
}
