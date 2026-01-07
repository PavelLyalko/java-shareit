package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import liquibase.util.StringClauses;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.ItemResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return item;
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (item.getOwner().getId() != userId) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на удаление предмета с id " + itemId);
        }
        itemRepository.delete(item);
    }

    @Override
    public List<ItemResponse> getItems(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(i -> {
                    ItemResponse itemResponse = new ItemResponse();
                    itemResponse.setId(i.getId());
                    itemResponse.setName(i.getName());
                    itemResponse.setDescription(i.getDescription());
                    itemResponse.setOwner(i.getOwner());
                    itemResponse.setAvailable(i.getAvailable());
                    Booking lastBooking = getLastBooking(i.getBookings());
                    Booking nextBooking = getNextBooking(i.getBookings());

                    itemResponse.setLastBookingStartDate(lastBooking.getStart());
                    itemResponse.setLastBookingEndDate(lastBooking.getEnd());
                    itemResponse.setNextBookingStartDate(nextBooking.getStart());
                    itemResponse.setNextBookingEndDate(nextBooking.getEnd());
                    return itemResponse;
                })
                .toList();
    }

    private Booking getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> BookingStatus.APPROVED == b.getStatus())
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> BookingStatus.APPROVED == b.getStatus())
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item editItem(long userId, ItemDto itemDto, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (userId != item.getOwner().getId()) {
            throw new InvalidUserException("Пользователь с id " + userId + " не имеет прав на редактирование предмета с id " + itemId);
        }
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }
        if (!item.getAvailable().equals(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return item;
    }

    @Override
    public Item getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        if (userId != item.getOwner().getId()) {
            throw new InvalidAccessException("Пользователь с id " + userId + " не имеет прав на получение предмета с id " + itemId);
        }
        return item;
    }

    @Override
    public List<Item> potentialItems(String text, long userId) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return null; //itemRepository.potentialItems(text);
    }

    @Transactional
    @Override
    public void addComment(CommentDto commentDto) {
        if (!bookingRepository.existsBookingByBookerIdAndItemId(commentDto.getUserId(), commentDto.getItemId())) {
            throw new InvalidAccessException("Пользователь не делал бронирование этой вещи");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(userRepository.findById(commentDto.getUserId()).orElse(null));
        comment.setItem(itemRepository.findById(commentDto.getItemId()).orElse(null));
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
    }
}
