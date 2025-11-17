package kg.spring.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TableCreationRequest(
        @NotNull @NotBlank @Pattern(regexp = "^(?!pg_)(?!app_)[a-z0-9_]+$") @Size(min = 3, max = 63)
        String tableName,
        @Size(max = 255)
        String userFriendlyName,
        @NotEmpty
        List<ColumnRequest> columns
) {
}