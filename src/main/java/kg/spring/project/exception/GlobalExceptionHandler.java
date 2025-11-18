package kg.spring.project.exception;

import kg.spring.project.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateTableException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTablesException(DuplicateTableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.toString(),
                Instant.now(),
                "/api/v1/dynamic-tables/schemas"
        );
        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(TableNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTableNotFoundException(TableNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.toString(),
                Instant.now(),
                ex.getPath()
        );
        return ResponseEntity.status(404).body(errorResponse);
    }
}
