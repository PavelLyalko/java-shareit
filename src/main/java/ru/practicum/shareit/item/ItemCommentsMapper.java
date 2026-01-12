package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemCommentsMapper {
    public static ItemCommentsResponse toItemCommentsResponse(Item item) {
        ItemCommentsResponse itemCommentsResponse = new ItemCommentsResponse();
        itemCommentsResponse.setId(item.getId());
        itemCommentsResponse.setName(item.getName());
        itemCommentsResponse.setAvailable(item.getAvailable());
        itemCommentsResponse.setDescription(item.getDescription());

        return itemCommentsResponse;
    }
}
