package kg.spring.project.mapper;

import kg.spring.project.dto.response.TableCreatedResponse;
import kg.spring.project.model.Table;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ColumnMapper.class)
public interface TableMapper {
    TableCreatedResponse toTableCreatedResponse(Table table);
}
