package kg.spring.project.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.exception.DuplicateTableException;
import kg.spring.project.exception.MissingColumnException;
import kg.spring.project.exception.NullValueForNonNullColumnException;
import kg.spring.project.exception.TableNotFoundException;
import kg.spring.project.exception.UnexpectedColumnException;
import kg.spring.project.model.Column;
import kg.spring.project.model.Table;
import kg.spring.project.repository.MainRepository;
import kg.spring.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final MainRepository repository;

    @Transactional
    public Table createTable(TableCreationRequest request) {
        if (repository.isTableExists(request.tableName())) throw new DuplicateTableException("Table already exists");
        repository.insertTableDefinition(request.tableName(), request.userFriendlyName());
        Long tableId = repository.getTableIdByName(request.tableName());
        repository.createDynamicTable(tableId, request);
        return getTableByName(request.tableName());
    }

    public Table getTableByName(String tableName) {
        Table table = repository.getTableByName(tableName);
        if (table == null) throw new TableNotFoundException("Table not found", "/api/v1/dynamic-tables/schemas/" + tableName);
        return table;
    }

    public ObjectNode insertDataIntoTable(String tableName, JsonNode data) {
        boolean tableExists = repository.isTableExists(tableName);
        if (!tableExists) throw new TableNotFoundException("Table not found", "/api/v1/dynamic-tables/data/" + tableName);

        Table table = repository.getTableByName(tableName);

        List<Column> columns = table.columns();

        Map<String, JsonNode> dataMap = new HashMap<>();
        Iterator<String> stringIterator = data.fieldNames();
        while (stringIterator.hasNext()) {
            String fieldName = stringIterator.next();
            dataMap.put(fieldName, data.get(fieldName));
        }
        for (String key : dataMap.keySet()) {
            if (columns.stream().noneMatch(column -> column.name().equals(key))) {
                throw new UnexpectedColumnException("Unexpected column: " + key, "/api/v1/dynamic-tables/data/" + tableName);
            }
        }
        columns.forEach(column -> {
            if (!data.has(column.name())) throw new MissingColumnException("Missing column: " + column.name(), "/api/v1/dynamic-tables/data/" + tableName);
            if (!column.isNullable() && data.get(column.name()).isNull()) throw new NullValueForNonNullColumnException("Null value for non-nullable column: " + column.name(), "/api/v1/dynamic-tables/data/" + tableName);
        });

        try{
            return repository.insertDataIntoTable(tableName, data);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error inserting data: " + e.getMessage());
        }
    }

}
