package kg.spring.project.mapper;

import kg.spring.project.model.Column;
import kg.spring.project.model.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TableMapper implements RowMapper<Table> {

    private final ColumnMapper columnMapper;

    public Table mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String tableName = rs.getString("table_name");
        String userFriendlyName = rs.getString("user_friendly_name");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        return new Table(id, tableName, userFriendlyName, createdAt);
    }
}
