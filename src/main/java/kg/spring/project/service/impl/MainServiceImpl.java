package kg.spring.project.service.impl;

import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.exception.DuplicateTableException;
import kg.spring.project.exception.TableNotFoundException;
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

}
