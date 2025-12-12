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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-ID";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> get(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public Item add(@RequestHeader(X_SHARER_USER_ID_HEADER) Long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                           @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public Item editItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                        @PathVariable(name = "itemId") long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> potentialItems(@RequestHeader(X_SHARER_USER_ID_HEADER) long userId,
                                     @RequestParam("text") String text) {
        return itemService.potentialItems(text, userId);
    }
}