package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Component
public class RatingDbStorage {

    private final RatingRepository repository;
    private final Logger log;

    public RatingDbStorage(RatingRepository repository) {
        this.repository = repository;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public Rating getById(Integer id) {
        Optional<Rating> optionalRating = repository.findById(id);
        if (optionalRating.isEmpty()) {
            String errorMessage = "Рейтинг с id " + id + " не найден";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            return optionalRating.get();
        }
    }

    public Collection<Rating> findAll() {
        return repository.findAll();
    }

}
