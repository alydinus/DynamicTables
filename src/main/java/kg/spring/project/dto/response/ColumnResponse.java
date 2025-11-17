package kg.spring.project.dto.response;

public record ColumnResponse(
        String name,
        String type,
        String postgresType,
        boolean isNullable,
        boolean isPrimaryKey
) {
}
