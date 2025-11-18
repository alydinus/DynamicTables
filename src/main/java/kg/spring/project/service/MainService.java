package kg.spring.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.model.Table;

import java.util.List;

public interface MainService {
    Table createTable(TableCreationRequest request);

    Table getTableByName(String tableName);

    ObjectNode insertDataIntoTable(String tableName, JsonNode data);

    List<Table> getAllTables();
}
