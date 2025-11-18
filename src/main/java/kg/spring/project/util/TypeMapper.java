package kg.spring.project.util;

import org.springframework.stereotype.Component;

@Component
public class TypeMapper {
    public String mapToPostgresType(String type) {
        return switch (type.toUpperCase()) {
            case "TEXT" -> "TEXT";
            case "INTEGER" -> "INTEGER";
            case "BIGINT" -> "BIGINT";
            case "DECIMAL" -> "NUMERIC(19,4)";
            case "BOOLEAN" -> "BOOLEAN";
            case "DATE" -> "TEXT";
            case "TIMESTAMP" -> "TIMESTAMP WITHOUT TIME ZONE";
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }
}
