package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void replaceBlankNameToLogin() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }
}
