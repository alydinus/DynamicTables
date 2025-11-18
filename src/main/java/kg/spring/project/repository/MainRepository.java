package kg.spring.project.repository;

import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.model.Table;

public interface MainRepository {
    boolean isTableExists(String tableName);
    void insertTableDefinition(String tableName, String userFriendlyName);
    Long getTableIdByName(String tableName);
    Table getTableByName(String tableName);
    void createDynamicTable(Long tableId, TableCreationRequest request);
}
