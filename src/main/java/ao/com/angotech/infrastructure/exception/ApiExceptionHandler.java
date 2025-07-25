package ao.com.angotech.infrastructure.exception;

import ao.com.angotech.infrastructure.dto.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    private static final String BLUE = "\u001B[34m";

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request, BindingResult result) {

        List<String> errors = result.getFieldErrors().stream()
                .map(fieldError -> String.format(
                        "%sCampo%s '%s': %s%s%s (valor rejeitado: %s'%s'%s)",
                        RED, RESET,
                        fieldError.getField(),
                        YELLOW, fieldError.getDefaultMessage(), RESET,
                        RED, fieldError.getRejectedValue(), RESET
                ))
                .toList();

        logger.error("\n\n🔴 API ERROR - VALIDAÇÃO FALHOU:\n{}", String.join("\n", errors));

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "Campo(a) invalido(a)", result));
    }
}
