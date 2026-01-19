package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */

@Data
public class Film {
    int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    @Size(max = 200, message = "Максимальная длина описания фильма должна быть не больше 200 символов")
    String description;
    LocalDate releaseDate;
    int duration;
}
