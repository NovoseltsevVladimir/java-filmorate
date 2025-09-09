package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingDbStorage storage;

    public Collection<Rating> findAll() {
        return storage.findAll();
    }

    public Rating getById(Integer id) {
        return storage.getById(id);
    }
}
