package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Film.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма должна быть не больше 200 символов")
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Rating mpa;
    private Set<Genre> genres = new LinkedHashSet<>();
}

