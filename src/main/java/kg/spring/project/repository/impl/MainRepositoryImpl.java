package kg.spring.project.repository.impl;

import kg.spring.project.dto.request.ColumnRequest;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.exception.TableNotFoundException;
import kg.spring.project.mapper.extractor.TableModelExtractor;
import kg.spring.project.model.Table;
import kg.spring.project.repository.MainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MainRepositoryImpl implements MainRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TableModelExtractor tableResponseExtractor;

    public boolean isTableExists(String tableName) {
        Integer exists = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM app_dynamic_table_definitions WHERE table_name = ?",
                Integer.class, tableName);
        return exists != null && exists > 0;
    }

    public void insertTableDefinition(String tableName, String userFriendlyName) {
        jdbcTemplate.update(
                "INSERT INTO app_dynamic_table_definitions(table_name, user_friendly_name) VALUES (?, ?)",
                tableName, userFriendlyName
        );
    }

    public Long getTableIdByName(String tableName) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM app_dynamic_table_definitions WHERE table_name = ?",
                Long.class, tableName
        );
    }

    public Table getTableByName(String tableName) {
        Table table = jdbcTemplate.query(
                """
                        SELECT tables.id as table_id,
                               tables.table_name as table_name,
                               tables.user_friendly_name as user_friendly_name,
                               tables.created_at as table_created_at,
                               columns.id as column_id,
                               columns.table_definition_id as table_definition_id,
                               columns.column_name as column_name,
                               columns.column_type as column_type,
                               columns.postgres_column_type as postgres_column_type,
                               columns.is_nullable as is_nullable,
                               columns.is_primary_key_internal as is_primary_key_internal,
                               columns.created_at as column_created_at
                        FROM app_dynamic_column_definitions columns
                        JOIN app_dynamic_table_definitions tables
                        ON columns.table_definition_id = tables.id
                        WHERE tables.table_name = ?
                        """,
                tableResponseExtractor,
                tableName
        );
        return table;
    }

    public void createDynamicTable(Long tableId, TableCreationRequest request) {
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
}
