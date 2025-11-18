package kg.spring.project.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.model.Table;

public interface MainRepository {
    boolean isTableExists(String tableName);

    void insertTableDefinition(String tableName, String userFriendlyName);

    Long getTableIdByName(String tableName);

    Table getTableByName(String tableName);

    void createDynamicTable(Long tableId, TableCreationRequest request);

    ObjectNode insertDataIntoTable(String tableName, JsonNode data);
}
