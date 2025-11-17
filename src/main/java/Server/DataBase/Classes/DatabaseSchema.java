package Server.DataBase.Classes;

import java.util.List;

/**
 * Enum, що описує всю структуру бази даних.
 * Містить допоміжні класи Column, PrimaryKey та ForeignKey.
 */
public class DatabaseSchema {

    // Допоміжні записи (records) для опису структури
    public record Column(String name, String type) {}
    public record ForeignKey(String sourceColumn, String targetTable, String targetColumn, String onDelete) {
        public ForeignKey(String sourceColumn, String targetTable, String targetColumn) {
            this(sourceColumn, targetTable, targetColumn, "CASCADE"); // Поведінка за замовчуванням
        }
    }

    // Головний Enum, що описує таблиці
    public enum Table {
        USERS("users",
                List.of(
                        new Column("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                        new Column("login", "TEXT UNIQUE NOT NULL"),
                        new Column("password", "BLOB NOT NULL"),
                        new Column("salt", "BLOB NOT NULL")
                ),
                List.of(), // PK вже визначено в Column
                List.of()
        ),
        PUBLIC_KEYS("public_keys",
                List.of(
                        new Column("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                        new Column("user_id", "INTEGER NOT NULL"),
                        new Column("device_id", "TEXT NOT NULL"),
                        new Column("public_key", "TEXT NOT NULL")
                ),
                List.of(),
                List.of(new ForeignKey("user_id", "users", "id"))
        ),
        CHATS("chats",
                List.of(
                        new Column("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                        new Column("chat_name", "TEXT"),
                        new Column("is_group_chat", "BOOLEAN NOT NULL DEFAULT 0")
                ),
                List.of(),
                List.of()
        ),
        CHAT_USERS("chat_users",
                List.of(
                        new Column("user_id", "INTEGER NOT NULL"),
                        new Column("chat_id", "INTEGER NOT NULL")
                ),
                List.of("user_id", "chat_id"), // Складений Primary Key
                List.of(
                        new ForeignKey("user_id", "users", "id"),
                        new ForeignKey("chat_id", "chats", "id")
                )
        ),
        MESSAGES("messages",
                List.of(
                        new Column("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                        new Column("chat_id", "INTEGER NOT NULL"),
                        new Column("sender_id", "INTEGER NOT NULL"),
                        new Column("content", "BLOB NOT NULL"),
                        new Column("timestamp", "DATETIME DEFAULT CURRENT_TIMESTAMP"),
                        new Column("status", "TEXT DEFAULT 'sent'")
                ),
                List.of(),
                List.of(
                        new ForeignKey("chat_id", "chats", "id"),
                        new ForeignKey("sender_id", "users", "id")
                )
        );

        private final String tableName;
        private final List<Column> columns;
        private final List<String> primaryKeys;
        private final List<ForeignKey> foreignKeys;

        Table(String tableName, List<Column> columns, List<String> primaryKeys, List<ForeignKey> foreignKeys) {
            this.tableName = tableName;
            this.columns = columns;
            this.primaryKeys = primaryKeys;
            this.foreignKeys = foreignKeys;
        }

        public String getTableName() { return tableName; }
        public List<Column> getColumns() { return columns; }
        public List<String> getPrimaryKeys() { return primaryKeys; }
        public List<ForeignKey> getForeignKeys() { return foreignKeys; }
    }
}
