package kg.spring.project.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.spring.project.dto.request.ColumnRequest;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.exception.DataNotFoundException;
import kg.spring.project.mapper.extractor.TableListExtractor;
import kg.spring.project.mapper.extractor.TableModelExtractor;
import kg.spring.project.model.Table;
import kg.spring.project.repository.MainRepository;
import kg.spring.project.util.TypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MainRepositoryImpl implements MainRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TableModelExtractor tableResponseExtractor;
    private final TableListExtractor tableListExtractor;
    private final TypeMapper typeMapper;
    private final ObjectMapper objectMapper;

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
        return jdbcTemplate.query(
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
    }

    public boolean isDataExistsById(String tableName, Long id) {
        Long doesIdExist = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName + " WHERE id = ?", Long.class, id);
        return doesIdExist != null && doesIdExist > 0;
    }

    public void createDynamicTable(Long tableId, TableCreationRequest request) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE ").append(request.tableName()).append(" (");
        ddl.append("id BIGSERIAL PRIMARY KEY");

        for (ColumnRequest col : request.columns()) {
            String postgresType = typeMapper.mapToPostgresType(col.type());
            ddl.append(", ").append(col.name()).append(" ").append(postgresType);
            if (!col.isNullable()) ddl.append(" NOT NULL");
            jdbcTemplate.update("INSERT INTO app_dynamic_column_definitions(table_definition_id, column_name, column_type, postgres_column_type, is_nullable) VALUES (?,?,?,?,?)",
                    tableId, col.name(), col.type(), postgresType, col.isNullable());
        }
        ddl.append(");");
        jdbcTemplate.execute(ddl.toString());
    }

    public ObjectNode insertDataIntoTable(String tableName, JsonNode data) {
        Iterator<String> fields = data.fieldNames();
        Map<String, Object> jsonFields = new LinkedHashMap<>();

        while (fields.hasNext()) {
            String field = fields.next();
            jsonFields.put(field, convertJsonValue(data.get(field)));
        }

        String columns = String.join(", ", jsonFields.keySet());

        String placeholders = jsonFields.keySet().stream()
                .map(k -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ") RETURNING id";

        Long id = jdbcTemplate.queryForObject(sql, jsonFields.values().toArray(), Long.class);
        ObjectNode response = objectMapper.createObjectNode();
        response.put("id", id);
        jsonFields.forEach(response::putPOJO);
        return response;

    }

    public List<Table> getAllTables() {
        return jdbcTemplate.query(
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
                        """,
                tableListExtractor
        );
    }

    public Page<ObjectNode> getAllDataFromTable(String tableName, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        String countSql = "SELECT COUNT(*) FROM " + tableName;
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class);
        if (totalElements == null || totalElements == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        String dataSql = "SELECT * FROM " + tableName + " ORDER BY id ASC LIMIT ? OFFSET ?";

        List<ObjectNode> content = jdbcTemplate.query(
                dataSql,
                (rs, rowNum) -> {
                    ObjectNode row = objectMapper.createObjectNode();
                    int columnCount = rs.getMetaData().getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        Object value = rs.getObject(i);
                        if (value == null) {
                            row.putNull(columnName);
                        } else {
                            row.putPOJO(columnName, value);
                        }
                    }
                    return row;
                },
                pageable.getPageSize(),
                pageable.getOffset()
        );

        return new PageImpl<>(content, pageable, totalElements);
    }

    public ObjectNode getDataById(String tableName, Long id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    ObjectNode row = objectMapper.createObjectNode();
                    int columnCount = rs.getMetaData().getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        Object value = rs.getObject(i);
                        if (value == null) {
                            row.putNull(columnName);
                        } else {
                            row.putPOJO(columnName, value);
                        }
                    }
                    return row;
                },
                id
        );
    }

    public ObjectNode updateDataById(String tableName, Long id, JsonNode request) {
        Iterator<String> fields = request.fieldNames();
        Map<String, Object> jsonFields = new LinkedHashMap<>();

        while (fields.hasNext()) {
            String field = fields.next();
            jsonFields.put(field, convertJsonValue(request.get(field)));
        }

        String setClause = jsonFields.keySet().stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id = ?";

        List<Object> params = new ArrayList<>(jsonFields.values());
        params.add(id);

        jdbcTemplate.update(sql, params.toArray());

        return getDataById(tableName, id);
    }

    public Void deleteDataById(String tableName, Long id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return null;
    }

    private Object convertJsonValue(JsonNode node) {
        if (node.isInt()) return node.intValue();
        if (node.isLong()) return node.longValue();
        if (node.isDouble()) return node.doubleValue();
        if (node.isBoolean()) return node.booleanValue();
        if (node.isTextual()) return node.textValue();
        if (node.isNull()) return null;
        return node.toString();
    }

}
