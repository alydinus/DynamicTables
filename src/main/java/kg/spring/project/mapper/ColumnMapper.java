package kg.spring.project.mapper;

import kg.spring.project.dto.response.ColumnResponse;
import kg.spring.project.model.Column;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ColumnMapper {
    ColumnResponse toColumnResponse(Column column);
}
