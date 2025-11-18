package kg.spring.project.model;

import java.time.Instant;
import java.util.List;

public record Table(
        Long id,
        String tableName,
        String userFriendlyName,
        List<Column> columns,
        Instant createdAt
) {
}
