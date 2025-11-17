package kg.spring.project.mapper;

import kg.spring.project.model.Column;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
public class ColumnMapper implements RowMapper<Column> {
    public Column mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        Long tableId = rs.getLong("table_id");
        String name = rs.getString("column_name");
        String type = rs.getString("data_type");
        String postgresType = rs.getString("postgres_type");
        boolean isNullable = rs.getBoolean("is_nullable");
        boolean isPrimaryKey = rs.getBoolean("is_primary_key");
        Instant createdAt = rs.getTimestamp("created_at").toInstant();
        return new Column(id, tableId, name, type, postgresType, isNullable, isPrimaryKey, createdAt);
    }

}
