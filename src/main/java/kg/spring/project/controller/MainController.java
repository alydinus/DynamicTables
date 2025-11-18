package kg.spring.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.dto.response.TableCreatedResponse;
import kg.spring.project.mapper.TableMapper;
import kg.spring.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dynamic-tables")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final TableMapper tableMapper;

    @PostMapping("/schemas")
    public ResponseEntity<?> createTable(@RequestBody @Valid TableCreationRequest request) {
        return new ResponseEntity<>(tableMapper.toTableCreatedResponse(mainService.createTable(request)), HttpStatus.CREATED);
    }

    @GetMapping("/schemas/{tableName}")
    public ResponseEntity<TableCreatedResponse> getTable(@PathVariable String tableName) {
        return new ResponseEntity<>(tableMapper.toTableCreatedResponse(mainService.getTableByName(tableName)), HttpStatus.OK);
    }

    @GetMapping("/schemas")
    public ResponseEntity<List<TableCreatedResponse>> getAllTables() {
        return new ResponseEntity<>(mainService.getAllTables().stream().map(tableMapper::toTableCreatedResponse).toList(), HttpStatus.OK);
    }

    @PostMapping("/data/{tableName}")
    public ResponseEntity<?> insertData(@PathVariable String tableName, @RequestBody @Valid JsonNode request) {
        return new ResponseEntity<>(mainService.insertDataIntoTable(tableName, request), HttpStatus.OK);
    }
}
