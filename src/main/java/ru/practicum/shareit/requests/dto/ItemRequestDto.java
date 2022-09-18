package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull
    @Positive
    private long id;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    @Positive
    private long requester;

    @NotNull
    private LocalDateTime created = LocalDateTime.now();
    private List<ItemDto> items;
}