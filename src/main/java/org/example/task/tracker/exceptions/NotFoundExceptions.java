package org.example.task.tracker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс NotFoundExceptions представляет собой пользовательское исключение, которое используется
 * для обозначения ситуаций, когда запрашиваемый ресурс или элемент не найден в приложении.
 * Это исключение соответствует HTTP-статусу "404 Not Found".
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundExceptions extends RuntimeException {

    /**
     * Конструктор класса, который принимает сообщение об ошибке и передает его в конструктор
     * суперкласса (RuntimeException) для установки сообщения об ошибке.
     *
     * @param message Сообщение об ошибке, описывающее причину исключения.
     */
    public NotFoundExceptions(String message) {
        super(message);
    }
}
