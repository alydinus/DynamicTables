package kg.spring.project.service.impl;

import kg.spring.project.dto.request.ColumnRequest;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.dto.response.ColumnResponse;
import kg.spring.project.dto.response.TableCreatedResponse;
import kg.spring.project.exception.ConflictException;
import kg.spring.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public TableCreatedResponse createTable(TableCreationRequest request) {
        Integer exists = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM app_dynamic_table_definitions WHERE table_name = ?",
                Integer.class, request.tableName());
        if (exists != null && exists > 0) throw new ConflictException("Table already exists");
        jdbcTemplate.update(
                "INSERT INTO app_dynamic_table_definitions(table_name, user_friendly_name) VALUES (?, ?)",
                request.tableName(), request.userFriendlyName()
        );
        Long tableId = jdbcTemplate.queryForObject(
                "SELECT id FROM app_dynamic_table_definitions WHERE table_name = ?",
                Long.class, request.tableName()
        );
        createDynamicTable(tableId, request);

        return mapToResponse(tableId);
    }

    private void createDynamicTable(Long tableId, TableCreationRequest request) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE ").append(request.tableName()).append(" (");
        ddl.append("id BIGSERIAL PRIMARY KEY");

        for (ColumnRequest col : request.columns()) {
            String postgresType = mapToPostgresType(col.type());
            ddl.append(", ").append(col.name()).append(" ").append(postgresType);
            if (!col.isNullable()) ddl.append(" NOT NULL");
            jdbcTemplate.update("INSERT INTO app_dynamic_column_definitions(table_definition_id, column_name, column_type, postgres_column_type, is_nullable) VALUES (?,?,?,?,?)",
                    tableId, col.name(), col.type(), postgresType, col.isNullable());
        }
        ddl.append(");");
        jdbcTemplate.execute(ddl.toString());
    }

    private String mapToPostgresType(String type) {
        return switch (type.toUpperCase()) {
            case "TEXT" -> "TEXT";
            case "INTEGER" -> "INTEGER";
            case "BIGINT" -> "BIGINT";
            case "DECIMAL" -> "NUMERIC(19,4)";
            case "BOOLEAN" -> "BOOLEAN";
            case "DATE" -> "DATE";
            case "TIMESTAMP" -> "TIMESTAMP WITHOUT TIME ZONE";
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    private TableCreatedResponse mapToResponse(long tableId) {
        TableCreatedResponse table = jdbcTemplate.queryForObject("""
                    SELECT id, table_name, user_friendly_name
                    FROM app_dynamic_table_definitions
                    WHERE id = ?
                """, (rs, rowNum) -> new TableCreatedResponse(
                rs.getLong("id"),
                rs.getString("table_name"),
                rs.getString("user_friendly_name"),
                new ArrayList<>()
        ), tableId);

        List<ColumnResponse> columns = jdbcTemplate.query("""
                    SELECT column_name, column_type, postgres_column_type, is_nullable, is_primary_key_internal
                    FROM app_dynamic_column_definitions
                    WHERE table_definition_id = ?
                    ORDER BY id
                """, (rs, rowNum) -> new ColumnResponse(
                rs.getString("column_name"),
                rs.getString("column_type"),
                rs.getString("postgres_column_type"),
                rs.getBoolean("is_nullable"),
                rs.getBoolean("is_primary_key_internal")
        ), tableId);

        return new TableCreatedResponse(
                table.id(),
                table.tableName(),
                table.userFriendlyName(),
                columns
        );
    }
}
