package ru.practicum.shareit.booking.dto;

public class BookingDtoMapper {
    public static CreateBookingDto toCreateBookingDto(CreateBookingRequest createBookingRequest) {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(createBookingRequest.getStart());
        createBookingDto.setEnd(createBookingRequest.getEnd());
        createBookingDto.setItemId(createBookingRequest.getItemId());
        createBookingDto.setBookerId(createBookingRequest.getBookerId());
        return createBookingDto;
    }
}
