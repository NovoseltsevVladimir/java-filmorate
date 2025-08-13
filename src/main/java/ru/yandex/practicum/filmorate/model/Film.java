package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;

}
