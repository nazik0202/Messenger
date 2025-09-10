import java.sql.*;

public class SQLiteTest {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:accounts.db"; // файл у робочій директорії

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Створюємо таблицю користувачів
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY," +
                        "password BLOB NOT NULL)");
                System.out.println("Database is ready!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}