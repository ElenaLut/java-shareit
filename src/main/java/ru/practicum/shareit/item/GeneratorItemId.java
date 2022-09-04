package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

@Component
public class GeneratorItemId {
    private long id = 0;

    public long generate() {
        return ++id;
    }
}
