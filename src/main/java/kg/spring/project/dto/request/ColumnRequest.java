package kg.spring.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ColumnRequest(
        @NotNull @NotBlank @Size(max = 255) @Pattern(regexp = "^(?!pg_)(?!app_)(?!user)[a-z0-9_]+$")
        String name,
        @NotNull @NotBlank @Size(max = 50)
        String type,
        boolean isNullable
) {
        public ColumnRequest {
                if (!isNullable) {
                        isNullable = true;
                }
        }
}
