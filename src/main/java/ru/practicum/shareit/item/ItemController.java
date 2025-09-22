package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody ItemDto itemDto) {

        return itemService.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public Item editItem(@RequestHeader("X-Sharer-User-Id") long userId,
                         @PathVariable long itemId,
                         @RequestBody ItemDto itemDto) {
        return itemService.editItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                        @PathVariable(name = "itemId") long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> potentialItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam("text") String text) {
        return itemService.potentialItems(text, userId);
    }

}