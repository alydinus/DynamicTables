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

    @ExceptionHandler(UnexpectedColumnException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedColumnException(UnexpectedColumnException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                Instant.now(),
                ex.getPath()
        );
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(MissingColumnException.class)
    public ResponseEntity<ErrorResponse> handleMissingColumnException(MissingColumnException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                Instant.now(),
                ex.getPath()
        );
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(NullValueForNonNullColumnException.class)
    public ResponseEntity<ErrorResponse> handleNullValueForNonNullColumnException(NullValueForNonNullColumnException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.toString(),
                Instant.now(),
                ex.getPath()
        );
        return ResponseEntity.status(400).body(errorResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotFoundException ex) {
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
