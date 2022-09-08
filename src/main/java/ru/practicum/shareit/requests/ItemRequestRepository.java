package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}