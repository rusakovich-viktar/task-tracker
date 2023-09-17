package org.example.task.tracker.exceptions;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

/**
 * Класс CustomErrorController представляет собой кастомный контроллер ошибок для обработки ошибок,
 * которые могут возникнуть во время выполнения приложения. Он реализует интерфейс ErrorController
 * и обрабатывает ошибки, предоставляя информативные HTTP-ответы вместо стандартных страниц ошибок.
 * <p>
 * Этот контроллер обрабатывает ошибки, связанные с путем "/error", преобразует их в JSON-ответ
 * и возвращает ResponseEntity с информацией об ошибке в формате ErrorDto.
 *
 * @see ErrorController
 * @see ErrorDto
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    ErrorAttributes errorAttributes;

    /**
     * Обработчик ошибок, который принимает объект WebRequest и возвращает ResponseEntity с информацией
     * об ошибке в формате ErrorDto.
     *
     * @param request Объект WebRequest, связанный с ошибкой.
     * @return ResponseEntity с информацией об ошибке в формате ErrorDto.
     */
    @RequestMapping(CustomErrorController.PATH)
    public ResponseEntity<ErrorDto> error(WebRequest request) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.of
                (Include.EXCEPTION, Include.MESSAGE));

        return ResponseEntity
                .status((Integer) attributes.get("status"))
                .body(ErrorDto
                        .builder()
                        .error((String) attributes.get("error"))
                        .errorDescription((String) attributes.get("message"))
                        .build());
    }

}
