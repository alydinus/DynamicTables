package kg.spring.project.service;

import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.model.Table;

public interface MainService {
    Table createTable(TableCreationRequest request);

    Table getTableByName(String tableName);
}
