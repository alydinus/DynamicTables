package kg.spring.project.mapper.extractor;

import kg.spring.project.model.Column;
import kg.spring.project.model.Table;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableListExtractor implements ResultSetExtractor<List<Table>> {

    @Override
    public List<Table> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Table> tableMap = new LinkedHashMap<>();

        Map<Long, List<Column>> columnsBuffer = new HashMap<>();

        while (rs.next()) {
            long tableId = rs.getLong("table_id");

            List<Column> currentColumns = columnsBuffer.computeIfAbsent(tableId, k -> new ArrayList<>());

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

            currentColumns.add(column);

            if (!tableMap.containsKey(tableId)) {
                Table table = new Table(
                        tableId,
                        rs.getString("table_name"),
                        rs.getString("user_friendly_name"),
                        currentColumns,
                        rs.getTimestamp("table_created_at").toInstant()
                );
                tableMap.put(tableId, table);
            }
        }

        return new ArrayList<>(tableMap.values());
    }
}
