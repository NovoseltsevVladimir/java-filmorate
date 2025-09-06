package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Component
public class GenreDbStorage {

    private final GenreRepository repository;
    private final Logger log;

    @Autowired
    public GenreDbStorage(GenreRepository repository) {
        this.repository = repository;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public Genre getById(Integer id) {
        Optional<Genre> optionalGenre = repository.findById(id);
        if (optionalGenre.isEmpty()) {
            String errorMessage = "Жанр с id " + id + " не найден";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            return optionalGenre.get();
        }
    }

    public Collection<Genre> findAll() {
        return repository.findAll();
    }

}
