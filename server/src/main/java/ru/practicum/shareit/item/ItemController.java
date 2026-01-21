package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> get(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                           @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemCommentsResponse getItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                        @PathVariable(name = "itemId") long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemPotentialDto> potentialItems(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                                 @RequestParam("text") String text) {
        return itemService.potentialItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                      @PathVariable long itemId,
                                      @RequestBody CommentDto commentDto) {
        commentDto.setItemId(itemId);
        commentDto.setUserId(userId);
        return itemService.addComment(commentDto);
    }
}