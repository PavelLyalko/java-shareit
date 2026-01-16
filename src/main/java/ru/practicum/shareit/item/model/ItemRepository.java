package ru.practicum.shareit.item.model;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @EntityGraph(attributePaths = "bookings")
    List<Item> findAllByOwnerId(Long id);

    @EntityGraph(attributePaths = "bookings")
    Optional<Item> findById(Long id);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchByText(@Param("text") String text);
}
