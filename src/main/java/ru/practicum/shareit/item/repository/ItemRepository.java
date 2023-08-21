package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwner_Id(Long ownerId);

    List<Item> findItemByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String text1, String text2);

    @Query("SELECT i.id FROM Item AS i " +
            "JOIN User AS u ON i.owner.id=u.id " +
            "WHERE i.owner.id = :ownerId")
    List<Long> findAllItemIdByOwnerId(@Param("ownerId") Long ownerId);

    Boolean existsItemByOwnerId(Long ownerId);
}
