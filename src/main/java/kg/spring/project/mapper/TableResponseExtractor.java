package kg.spring.project.mapper;

import kg.spring.project.dto.response.ColumnResponse;
import kg.spring.project.dto.response.TableCreatedResponse;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TableResponseExtractor implements ResultSetExtractor<TableCreatedResponse> {
    public TableCreatedResponse extractData(ResultSet rs) throws SQLException {
        Long tableId = null;
        String tableName = null;
        String userFriendlyName = null;

        List<ColumnResponse> columns = new ArrayList<>();

        while (rs.next()) {
            if (tableId == null) {
                tableId = rs.getLong("table_id");
                tableName = rs.getString("table_name");
                userFriendlyName = rs.getString("user_friendly_name");
            }

            ColumnResponse column = new ColumnResponse(
                    rs.getString("column_name"),
                    rs.getString("column_type"),
                    rs.getString("postgres_column_type"),
                    rs.getBoolean("is_nullable"),
                    rs.getBoolean("is_primary_key_internal")
            );
            columns.add(column);
        }

        if (tableId == null) return null;

        return new TableCreatedResponse(
                tableId,
                tableName,
                userFriendlyName,
                columns
        );
    }
}
