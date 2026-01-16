package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidAccessException;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.dto.ItemResponse;
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

    @Transactional
    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Transactional
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
                .map(item -> {
                    Booking lastBooking = getLastBooking(item.getBookings());
                    Booking nextBooking = getNextBooking(item.getBookings());

                    LocalDateTime lastBookingStartDate = (lastBooking != null) ? lastBooking.getStart() : null;
                    LocalDateTime lastBookingEndDate = (lastBooking != null) ? lastBooking.getEnd() : null;
                    LocalDateTime nextBookingStartDate = (nextBooking != null) ? nextBooking.getStart() : null;
                    LocalDateTime nextBookingEndDate = (nextBooking != null) ? nextBooking.getEnd() : null;

                    return new ItemResponse(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            UserMapper.toUserDto(item.getOwner()),
                            lastBookingStartDate,
                            lastBookingEndDate,
                            null,
                            null,
                            nextBookingStartDate,
                            nextBookingEndDate
                    );
                })
                .toList();
    }


    private Booking getLastBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusSeconds(10);

        return bookings.stream()
                .filter(b -> BookingStatus.APPROVED == b.getStatus())
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(b -> b.getEnd().isBefore(threshold))
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

    @Transactional
    @Override
    public ItemDto editItem(long userId, ItemDto itemDto, long itemId) {
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
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public ItemCommentsResponse getItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        ItemCommentsResponse itemCommentsResponse = ItemCommentsMapper.toItemCommentsResponse(item);
        itemCommentsResponse.setNextBooking(getNextBooking(item.getBookings()));
        itemCommentsResponse.setLastBooking(getLastBooking(item.getBookings()));
        itemCommentsResponse.setComments(comments.stream().map(CommentMapper::toCommentResponse).toList());

        return itemCommentsResponse;
    }

    @Override
    public List<ItemPotentialDto> potentialItems(String text, long userId) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemPotentialDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentResponse addComment(CommentDto commentDto) {
        Booking booking = bookingRepository.findBookingByBookerIdAndItemId(commentDto.getUserId(), commentDto.getItemId());
        if (booking == null) {
            throw new InvalidAccessException("Пользователь не делал бронирование этой вещи");
        }

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new InvalidAccessException("Бронирование не подтверждено");
        }

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new InvalidAccessException("Бронирование ещё не завершено");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + commentDto.getUserId() + " не найден")));
        comment.setItem(itemRepository.findById(commentDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + commentDto.getItemId() + " не найдена")));
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return CommentMapper.toCommentResponse(comment);
    }
}
