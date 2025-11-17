package kg.spring.project.service;

import kg.spring.project.dto.request.TableCreationRequest;
import kg.spring.project.dto.response.TableCreatedResponse;

public interface MainService {
    TableCreatedResponse createTable(TableCreationRequest request);
}
