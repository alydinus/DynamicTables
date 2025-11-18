package kg.spring.project.mapper.extractor;

import kg.spring.project.model.Column;
import kg.spring.project.model.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class TableModelExtractor implements ResultSetExtractor<Table> {
    public Table extractData(ResultSet rs) throws SQLException, DataAccessException {
        Long tableId = null;
        String tableName = null;
        String userFriendlyName = null;
        Instant createdAt = null;

        List<Column> columns = new ArrayList<>();

        while (rs.next()) {
            if (tableId == null) {
                tableId = rs.getLong("table_id");
                tableName = rs.getString("table_name");
                userFriendlyName = rs.getString("user_friendly_name");
                createdAt = rs.getTimestamp("table_created_at").toInstant();
            }
            Column column = new Column(
                    rs.getLong("column_id"),
                    rs.getLong("table_definition_id"),
                    rs.getString("column_name"),
                    rs.getString("column_type"),
                    rs.getString("postgres_column_type"),
                    rs.getBoolean("is_nullable"),
                    rs.getBoolean("is_primary_key_internal"),
                    rs.getTimestamp("column_created_at").toInstant()
            );
            columns.add(column);
        }

        if (tableId == null) return null;

        return new Table(
                tableId,
                tableName,
                userFriendlyName,
                columns,
                createdAt
        );

    }
}
