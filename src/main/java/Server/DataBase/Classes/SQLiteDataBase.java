package Server.DataBase.Classes;

import java.sql.*;
import java.util.*;

/**
 * Базовий клас, що реалізує універсальні методи Database для SQLite.
 * Має "розумний" конструктор, що створює таблиці на основі схеми.
 */
public class SQLiteDataBase implements Database {

    protected static final String url = "jdbc:sqlite:src/main/java/Server/DataBase/Data/messenger.db";

    /**
     * Універсальний конструктор.
     * Отримує опис таблиці з ENUM і створює її, якщо вона не існує.
     * @param table Схема таблиці з DatabaseSchema.Table
     */
    public SQLiteDataBase(DatabaseSchema.Table table) {
        String createTableSql = buildCreateTableSql(table);
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSql);
            // System.out.println("Table " + table.getTableName() + " is ready.");

        } catch (SQLException e) {
            System.out.println("Database constructor error for table " + table.getTableName() + ": " + e.getMessage());
            throw new RuntimeException("Error initializing database table", e);
        }
    }

    // Допоміжний метод для побудови SQL-запиту
    private String buildCreateTableSql(DatabaseSchema.Table table) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");

        // 1. Додаємо колонки
        for (DatabaseSchema.Column col : table.getColumns()) {
            sql.append(col.name()).append(" ").append(col.type()).append(", ");
        }

        // 2. Додаємо складені Primary Keys (якщо є)
        if (!table.getPrimaryKeys().isEmpty()) {
            sql.append("PRIMARY KEY (").append(String.join(", ", table.getPrimaryKeys())).append("), ");
        }

        // 3. Додаємо Foreign Keys
        for (DatabaseSchema.ForeignKey fk : table.getForeignKeys()) {
            sql.append("FOREIGN KEY (").append(fk.sourceColumn()).append(") ")
                    .append("REFERENCES ").append(fk.targetTable()).append(" (").append(fk.targetColumn()).append(") ")
                    .append("ON DELETE ").append(fk.onDelete()).append(", ");
        }

        // Видаляємо останню кому і пробіл
        sql.delete(sql.length() - 2, sql.length());
        sql.append(");");

        // System.out.println("Generated SQL: " + sql.toString());
        return sql.toString();
    }


    @Override
    public List<Map<String, Object>> read(String tableName, List<String> returnFields, List<String> searchFields, List<Object> searchValues) throws SQLException {
        if (returnFields == null || returnFields.isEmpty()) {
            throw new SQLException("List of fields to return cannot be empty.");
        }

        String fields = String.join(", ", returnFields);
        String sql = "SELECT " + fields + " FROM " + tableName + buildWhereClause(searchFields);

        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, searchValues);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (String field : returnFields) {
                    row.put(field, rs.getObject(field));
                }
                results.add(row);
            }
        }
        return results;
    }

    @Override
    public long write(String tableName, List<String> fieldsToInsert, List<Object> valuesToInsert) throws SQLException {
        if (fieldsToInsert == null || valuesToInsert == null || fieldsToInsert.size() != valuesToInsert.size()) {
            throw new SQLException("Fields and values lists must be non-null and of the same size.");
        }


        String fields = String.join(", ", fieldsToInsert);
        String placeholders = String.join(", ", Collections.nCopies(valuesToInsert.size(), "?"));
        String sql = "INSERT INTO " + tableName + " (" + fields + ") VALUES (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(pstmt, valuesToInsert);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating record failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    return affectedRows;
                }
            }
        }
    }

    @Override
    public int edit(String tableName, List<String> updateFields, List<Object> updateValues, List<String> searchFields, List<Object> searchValues) throws SQLException {
        String setClause = String.join(" = ?, ", updateFields) + " = ?";
        String sql = "UPDATE " + tableName + " SET " + setClause + buildWhereClause(searchFields);

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            List<Object> allParams = new ArrayList<>(updateValues);
            if (searchValues != null) {
                allParams.addAll(searchValues);
            }

            setParameters(pstmt, allParams);
            return pstmt.executeUpdate();
        }
    }

    @Override
    public int delete(String tableName, List<String> searchFields, List<Object> searchValues) throws SQLException {
        String sql = "DELETE FROM " + tableName + buildWhereClause(searchFields);

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, searchValues);
            return pstmt.executeUpdate();
        }
    }

    // --- ПРИВАТНІ ДОПОМІЖНІ МЕТОДИ ---

    private String buildWhereClause(List<String> searchFields) {
        if (searchFields == null || searchFields.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" = ? AND ", searchFields) + " = ?";
    }

    private void setParameters(PreparedStatement pstmt, List<Object> values) throws SQLException {
        if (values == null) return;
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (value instanceof String) {
                pstmt.setString(i + 1, (String) value);
            } else if (value instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) value);
            } else if (value instanceof byte[]) {
                pstmt.setBytes(i + 1, (byte[]) value);
            } else if (value instanceof Long) {
                pstmt.setLong(i + 1, (Long) value);
            } else if (value instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) value);
            } else if (value == null) {
                pstmt.setNull(i + 1, Types.NULL);
            } else {
                pstmt.setObject(i + 1, value);
            }
        }
    }
}
