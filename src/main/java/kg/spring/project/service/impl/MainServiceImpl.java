package kg.spring.project.service.impl;

import kg.spring.project.dto.request.ColumnRequest;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.dto.response.TableCreatedResponse;
import kg.spring.project.exception.DuplicateTableException;
import kg.spring.project.exception.TableNotFoundException;
import kg.spring.project.mapper.TableMapper;
import kg.spring.project.mapper.extractor.TableResponseExtractor;
import kg.spring.project.model.Table;
import kg.spring.project.repository.MainRepository;
import kg.spring.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final MainRepository repository;
    private final TableMapper tableMapper;
    private final TableResponseExtractor tableResponseExtractor;

    @Transactional
    public Table createTable(TableCreationRequest request) {
        if (repository.isTableExists(request.tableName())) throw new DuplicateTableException("Table already exists");
        repository.insertTableDefinition(request.tableName(), request.userFriendlyName());
        Long tableId = repository.getTableIdByName(request.tableName());
        repository.createDynamicTable(tableId, request);
        return getTableByName(request.tableName());
    }

    public Table getTableByName(String tableName) {
        return repository.getTableByName(tableName);
    }

//    private void createDynamicTable(Long tableId, TableCreationRequest request) {
//        StringBuilder ddl = new StringBuilder();
//        ddl.append("CREATE TABLE ").append(request.tableName()).append(" (");
//        ddl.append("id BIGSERIAL PRIMARY KEY");
//
//        for (ColumnRequest col : request.columns()) {
//            String postgresType = mapToPostgresType(col.type());
//            ddl.append(", ").append(col.name()).append(" ").append(postgresType);
//            if (!col.isNullable()) ddl.append(" NOT NULL");
//            jdbcTemplate.update("INSERT INTO app_dynamic_column_definitions(table_definition_id, column_name, column_type, postgres_column_type, is_nullable) VALUES (?,?,?,?,?)",
//                    tableId, col.name(), col.type(), postgresType, col.isNullable());
//        }
//        ddl.append(");");
//        jdbcTemplate.execute(ddl.toString());
//    }
//
//    private String mapToPostgresType(String type) {
//        return switch (type.toUpperCase()) {
//            case "TEXT" -> "TEXT";
//            case "INTEGER" -> "INTEGER";
//            case "BIGINT" -> "BIGINT";
//            case "DECIMAL" -> "NUMERIC(19,4)";
//            case "BOOLEAN" -> "BOOLEAN";
//            case "DATE" -> "DATE";
//            case "TIMESTAMP" -> "TIMESTAMP WITHOUT TIME ZONE";
//            default -> throw new IllegalArgumentException("Unsupported type: " + type);
//        };
//    }
//
//    private TableCreatedResponse mapToResponse(long tableId) {
//        return jdbcTemplate.query(
//                """
//                        SELECT tables.id as table_id,
//                               tables.table_name as table_name,
//                               tables.user_friendly_name as user_friendly_name,
//                               columns.column_name as column_name,
//                               columns.column_type as column_type,
//                               columns.postgres_column_type as postgres_column_type,
//                               columns.is_nullable as is_nullable,
//                               columns.is_primary_key_internal as is_primary_key_internal
//                        FROM app_dynamic_column_definitions columns
//                        JOIN app_dynamic_table_definitions tables
//                        ON columns.table_definition_id = tables.id
//                        WHERE tables.id = ?
//                        """,
//                tableResponseExtractor,
//                tableId
//        );
//    }
}
