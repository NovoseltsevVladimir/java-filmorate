package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getTimestamp("releaseDate").toLocalDateTime().toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        String mpaName = resultSet.getString("mpaName");
        if (mpaName != null) {
            Rating mpa = new Rating();
            mpa.setName(mpaName);
            mpa.setId(resultSet.getInt("mpaId"));
            film.setMpa(mpa);
        }

        return film;
    }
}

