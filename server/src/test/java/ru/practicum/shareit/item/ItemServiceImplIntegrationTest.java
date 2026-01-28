package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void addNewItem_returnsItemDto() {
        // Подготовка данных
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        ItemDto result = itemService.addNewItem(user.getId(), itemDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void addNewItem_withInvalidUserId_throwsException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> itemService.addNewItem(999L, itemDto));
    }

    @Test
    void deleteItem_deletesItem() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        itemService.deleteItem(user.getId(), item.getId());

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), item.getId()));
    }

    @Test
    void deleteItem_withInvalidUserId_throwsException() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        assertThrows(InvalidAccessException.class, () -> itemService.deleteItem(999L, item.getId()));
    }

    @Test
    void getItems_returnsItemResponses() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        List<ItemResponse> items = itemService.getItems(user.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getName()).isIn("Item1", "Item2");
        assertThat(items.get(1).getName()).isIn("Item1", "Item2");
    }

    @Test
    void editItem_updatesItemAndReturnsDto() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("OldItem");
        item.setDescription("OldDescription");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("NewItem");
        itemDto.setDescription("NewDescription");
        itemDto.setAvailable(false);

        ItemDto result = itemService.editItem(user.getId(), itemDto, item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("NewItem");
        assertThat(result.getDescription()).isEqualTo("NewDescription");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void editItem_withInvalidUserId_throwsException() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("NewItem");
        itemDto.setDescription("NewDescription");
        itemDto.setAvailable(false);

        assertThrows(InvalidUserException.class, () -> itemService.editItem(999L, itemDto, item.getId()));
    }

    @Test
    void getItem_returnsItemCommentsResponse() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        ItemCommentsResponse result = itemService.getItem(user.getId(), item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Item");
        assertThat(result.getDescription()).isEqualTo("Description");
    }

    @Test
    void potentialItems_returnsItemPotentialDtos() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        List<ItemPotentialDto> items = itemService.potentialItems("Description", user.getId());

        assertThat(items).hasSize(2);
    }

    @Test
    void addComment_returnsCommentResponse() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        commentDto.setUserId(user.getId());
        commentDto.setItemId(item.getId());

        CommentResponse result = itemService.addComment(commentDto);

        assertThat(result.getText()).isEqualTo("Comment");
        assertThat(result.getAuthorName()).isEqualTo("User");
    }

    @Test
    void addComment_withNoBooking_throwsException() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setName("User");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment");
        commentDto.setUserId(user.getId());
        commentDto.setItemId(item.getId());

        assertThrows(InvalidAccessException.class, () -> itemService.addComment(commentDto));
    }
}
