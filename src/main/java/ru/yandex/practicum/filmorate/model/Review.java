package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer reviewId;
    private String content;
    @JsonProperty("isPositive")
    private Boolean positive;
    private Integer userId;
    private Integer filmId;
    private int useful;
}
