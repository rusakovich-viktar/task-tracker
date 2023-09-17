package org.example.task.tracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс BadRequestExceptions представляет собой пользовательское исключение, которое используется для
 * обозначения ситуаций, связанных с некорректными запросами в веб-приложении. Это исключение будет
 * выбрасываться, когда запрос клиента не соответствует ожиданиям и требованиям вашего приложения.
 * Например, это может быть использовано для обработки некорректных параметров запроса.
 * <p>
 * Класс аннотирован @ResponseStatus(HttpStatus.BAD_REQUEST), что указывает на то, что данное исключение
 * соответствует HTTP-статусу "400 Bad Request". Это означает, что клиент отправил недействительный или
 * некорректный запрос.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestExceptions extends RuntimeException {

    /**
     * Конструктор класса, который принимает сообщение об ошибке и передает его в конструктор
     * суперкласса (RuntimeException) для установки сообщения об ошибке.
     *
     * @param message Сообщение об ошибке, описывающее причину исключения.
     */
    public BadRequestExceptions(String message) {
        super(message);
    }
}
