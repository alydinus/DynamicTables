package kg.spring.project.dto.response;

import java.util.List;

public record TableCreatedResponse(
        Long id,
        String tableName,
        String userFriendlyName,
        List<ColumnResponse> columns
) {
}
