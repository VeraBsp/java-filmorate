package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feed {
    @Positive
    private Integer eventId;
    @NotBlank
    private String eventType;
    @NotBlank
    private String operation;
    private long eventTimestamp;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer entityId;
}
