package kg.spring.project.dto.response;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        String error,
        Instant timestamp,
        String path
) {

}
