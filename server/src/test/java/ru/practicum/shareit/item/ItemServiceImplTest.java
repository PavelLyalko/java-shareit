package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemCommentsResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPotentialDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addNewItem_callsRepositories_andReturnsDto() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        Item mapped = new Item();
        mapped.setName("Drill");
        mapped.setDescription("Good drill");
        mapped.setAvailable(true);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(mapped);

        doAnswer(inv -> {
            Item i = inv.getArgument(0);
            i.setId(10L);
            return i;
        }).when(itemRepository).save(any(Item.class));

        ItemDto result = itemService.addNewItem(1L, new ItemDto(null, "Drill", "Good drill", true, 1L));

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());
        assertThat(captor.getValue().getOwner().getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(requestRepository).findById(1L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Drill");
    }

    @Test
    void deleteItem_findsItem_andDeletes() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(2L);
        item.setOwner(owner);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(any(Item.class));

        itemService.deleteItem(1L, 2L);

        verify(itemRepository).findById(2L);
        verify(itemRepository).delete(item);
    }

    @Test
    void getItems_callsFindAllByOwnerId_andReturnsResponses() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setBookings(List.of());
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemResponse> result = itemService.getItems(1L);

        verify(itemRepository).findAllByOwnerId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Drill");
    }

    @Test
    void editItem_findsItem_saves_andReturnsDto() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(2L);
        item.setOwner(owner);
        item.setName("Old");
        item.setDescription("Old");
        item.setAvailable(true);
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        doAnswer(inv -> inv.getArgument(0)).when(itemRepository).save(any(Item.class));

        ItemDto result = itemService.editItem(1L, new ItemDto(null, "New", "New desc", false, 0L), 2L);

        verify(itemRepository).findById(2L);
        verify(itemRepository).save(item);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void getItem_loadsComments_andReturnsResponse() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(2L);
        item.setOwner(owner);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);
        item.setBookings(List.of());
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setText("Nice");
        comment.setAuthor(new User(3L, "u@mail.com", "User"));
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(commentRepository.findAllByItemId(2L)).thenReturn(List.of(comment));

        ItemCommentsResponse result = itemService.getItem(1L, 2L);

        verify(itemRepository).findById(2L);
        verify(commentRepository).findAllByItemId(2L);
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Nice");
    }

    @Test
    void potentialItems_whenTextNotEmpty_callsSearchByText_andReturnsDtos() {
        User owner = new User(1L, "owner@mail.com", "Owner");
        Item item = new Item();
        item.setId(3L);
        item.setName("Saw");
        item.setDescription("Hand saw");
        item.setAvailable(true);
        item.setOwner(owner);
        when(itemRepository.searchByText("sa")).thenReturn(List.of(item));

        List<ItemPotentialDto> result = itemService.potentialItems("sa", 1L);

        verify(itemRepository).searchByText("sa");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        assertThat(result.get(0).getOwner().getId()).isEqualTo(1L);
    }

    @Test
    void potentialItems_whenTextEmpty_returnsEmptyList() {
        List<ItemPotentialDto> result = itemService.potentialItems("", 1L);
        assertThat(result).isEmpty();
    }

    @Test
    void addComment_savesComment_andReturnsResponse() {
        long userId = 1L;
        long itemId = 2L;

        Item item = new Item();
        item.setId(itemId);
        item.setName("Drill");
        item.setDescription("Good drill");
        item.setAvailable(true);

        User author = new User(userId, "u@mail.com", "User");

        Booking booking = new Booking();
        booking.setId(50L);
        booking.setItem(item);
        booking.setBooker(author);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusMinutes(1));

        when(bookingRepository.findBookingByBookerIdAndItemId(userId, itemId)).thenReturn(booking);
        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(100L);
            return c;
        }).when(commentRepository).save(any(Comment.class));

        CommentDto dto = new CommentDto();
        dto.setText("Nice item");
        dto.setUserId(userId);
        dto.setItemId(itemId);

        CommentResponse result = itemService.addComment(dto);

        verify(bookingRepository).findBookingByBookerIdAndItemId(userId, itemId);
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(commentRepository).save(any(Comment.class));
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getText()).isEqualTo("Nice item");
        assertThat(result.getAuthorName()).isEqualTo("User");
        assertThat(result.getCreated()).isNotNull();
    }
}

