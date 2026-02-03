package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    int id;
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @NotBlank(message = "Логин не может быть пустым")
    String login;
    String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}
