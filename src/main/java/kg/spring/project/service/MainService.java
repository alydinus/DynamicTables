package kg.spring.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.model.Table;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MainService {
    Table createTable(TableCreationRequest request);

    Table getTableByName(String tableName);

    ObjectNode insertDataIntoTable(String tableName, JsonNode data);

    List<Table> getAllTables();

    Page<ObjectNode> getAllDataFromTable(String tableName, Integer page, Integer size);

    ObjectNode getDataById(String tableName, Long id);

    ObjectNode updateDataById(String tableName, Long id, @Valid JsonNode request);

    Void deleteDataById(String tableName, Long id);
}
