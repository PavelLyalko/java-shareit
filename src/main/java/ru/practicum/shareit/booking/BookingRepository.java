package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    List<Booking> findAllBookingsByBookerId(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.start <= :now AND :now <= b.end AND b.booker = :bookerId ")
    List<Booking> findAllByBookerIdAndCurrentBookings(@Param("bookerId") long bookerId, @Param("now") LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartAfter(long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndEndBefore(long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN b.item i JOIN i.owner u WHERE b.start <= :now AND :now <= b.end AND u.id = :ownerId")
    List<Booking> findAllByItemOwnerIdAndCurrentBookings(@Param("ownerId") long ownerId, @Param("now") LocalDateTime now);

}
