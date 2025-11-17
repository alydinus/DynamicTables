package kg.spring.project.model;

import java.time.Instant;

public record Column(
        Long id,
        Long tableId,
        String name,
        String type,
        String postgresType,
        boolean isNullable,
        boolean isPrimaryKey,
        Instant createdAt
) {
}
