package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
public class Film {
    private int id;
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма должна быть не больше 200 символов")
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes = new HashSet<>();
}

