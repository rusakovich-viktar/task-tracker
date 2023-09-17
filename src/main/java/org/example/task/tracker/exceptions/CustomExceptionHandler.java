package org.example.task.tracker.exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Класс CustomExceptionHandler представляет собой глобальный обработчик исключений для Spring приложения.
 * Он используется для логирования и обработки исключений, которые возникают в контроллерах приложения.
 * Этот класс расширяет ResponseEntityExceptionHandler, чтобы предоставить стандартные методы
 * для обработки исключений, связанных с HTTP-запросами, и возврата соответствующих HTTP-ответов.
 * <p>
 * Аннотация @ControllerAdvice указывает, что этот класс является глобальным советником для контроллеров
 * и будет обрабатывать исключения, возникающие во всех контроллерах приложения.
 * <p>
 * Метод exception() является обработчиком исключений и выполняет логирование исключения,
 * а затем передает управление методу handleException() для обработки и возврата HTTP-ответа.
 */
@Log4j2
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Обработчик исключений, который выполняет логирование исключения и вызывает метод
     * handleException() для обработки и возврата HTTP-ответа.
     *
     * @param exception Исключение, которое необходимо обработать.
     * @param request   Запрос, связанный с исключением.
     * @return HTTP-ответ, сгенерированный на основе обработки исключения.
     * @throws Exception Если происходит ошибка при обработке исключения.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception exception, WebRequest request) throws Exception {
        log.error("Exception during execution of application", exception);
        return handleException(exception, request);
    }
}
