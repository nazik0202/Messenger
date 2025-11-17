package Server.DataBase.Classes;

import Server.DataBase.Classes.DatabaseSchema;
import Server.DataBase.Classes.SQLiteDataBase;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Специфічний клас для роботи з таблицею Users.
 * Наслідується від SQLiteDataBase і використовує його загальні методи.
 */
public class AutorizationDB extends SQLiteDataBase {

    private static final String TABLE_NAME = DatabaseSchema.Table.USERS.getTableName();

    /**
     * Конструктор.
     * Викликає батьківський конструктор, передаючи йому схему таблиці USERS.
     * Це гарантує, що таблиця "users" буде створена, якщо її не існує.
     */
    public AutorizationDB() {
        super(DatabaseSchema.Table.USERS);
    }

    // --- Специфічні методи ---

    public byte[] readPassword(String login) {
        try {
            // Викликаємо загальний метод read() з базового класу
            List<Map<String, Object>> result = super.read(
                    TABLE_NAME,
                    List.of("password"),     // Поле, яке хочемо отримати
                    List.of("login"),        // Поле, за яким шукаємо
                    List.of(login)           // Значення, яке шукаємо
            );

            if (result.isEmpty()) {
                return null; // Немає такого логіна
            }
            // Повертаємо результат
            return (byte[]) result.get(0).get("password");

        } catch (SQLException e) {
            System.out.println("Error reading password: " + e.getMessage());
            throw new RuntimeException("Error reading password", e);
        }
    }

    public byte[] readSL(String login) {
        try {
            // Аналогічно викликаємо read()
            List<Map<String, Object>> result = super.read(
                    TABLE_NAME,
                    List.of("salt"),
                    List.of("login"),
                    List.of(login)
            );

            if (result.isEmpty()) {
                return null;
            }
            return (byte[]) result.get(0).get("salt");

        } catch (SQLException e) {
            System.out.println("Error reading salt: " + e.getMessage());
            throw new RuntimeException("Error reading salt", e);
        }
    }

    public void writeUser(String login, byte[] password, byte[] salt) {
        try {
            // Викликаємо загальний метод write() з базового класу
            super.write(
                    TABLE_NAME,
                    List.of("login", "password", "salt"),  // Поля, в які пишемо
                    List.of(login, password, salt)         // Значення
            );
        } catch (SQLException e) {
            System.out.println("Error writing user: " + e.getMessage());
            throw new RuntimeException("Error writing user", e);
        }
    }

    public void deleteUser(String login) {
        try {
            // Викликаємо загальний метод delete()
            super.delete(
                    TABLE_NAME,
                    List.of("login"),
                    List.of(login)
            );
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Error deleting user", e);
        }
    }
}
