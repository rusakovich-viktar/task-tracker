package org.example.task.tracker.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Класс ErrorDto представляет собой объект данных (DTO), используемый для представления информации об ошибке
 * в формате JSON. Этот класс содержит два поля: error (ошибка) и errorDescription (описание ошибки).
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDto {

    String error;

    @JsonProperty("error_description")
    String errorDescription;
}
