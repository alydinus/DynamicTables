package kg.spring.project.controller;

import jakarta.validation.Valid;
import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dynamic-tables")
@RequiredArgsConstructor
public class MainController {


    private final MainService mainService;

    @PostMapping("/schemas")
    public ResponseEntity<?> createSchema(@RequestBody @Valid TableCreationRequest request) {
        return new ResponseEntity<>(mainService.createTable(request), HttpStatus.CREATED);
    }
}
