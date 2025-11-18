package kg.spring.project.controller;

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
}
