package Server.DataBase.Classes;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Універсальний інтерфейс для операцій з базою даних.
 * Використовує PreparedStatement для запобігання SQL-ін'єкціям.
 */
public interface Database {

    /**
     * Читає дані з таблиці.
     * @param tableName Назва таблиці.
     * @param returnFields Список полів, які потрібно повернути.
     * @param searchFields Список полів для WHERE-фільтра (напр., "login").
     * @param searchValues Список значень для WHERE-фільтра (напр., "user123").
     * @return Список Map, де кожна Map - це один рядок (ключ=назва поля, значення=дані).
     */
    List<Map<String, Object>> read(String tableName, List<String> returnFields, List<String> searchFields, List<Object> searchValues) throws SQLException;

    /**
     * Записує новий рядок у таблицю.
     * @param tableName Назва таблиці.
     * @param fieldsToInsert Список полів для запису (напр., "login", "password").
     * @param valuesToInsert Список значень для запису.
     * @return Згенерований ID (якщо є AUTOINCREMENT), або кількість змінених рядків.
     */
    long write(String tableName, List<String> fieldsToInsert, List<Object> valuesToInsert) throws SQLException;

    /**
     * Редагує існуючі рядки в таблиці.
     * @param tableName Назва таблиці.
     * @param updateFields Список полів, які треба оновити.
     * @param updateValues Нові значення для цих полів.
     * @param searchFields Список полів для WHERE-фільтра.
     * @param searchValues Список значень для WHERE-фільтра.
     * @return Кількість оновлених рядків.
     */
    int edit(String tableName, List<String> updateFields, List<Object> updateValues, List<String> searchFields, List<Object> searchValues) throws SQLException;

    /**
     * Видаляє рядки з таблиці.
     * @param tableName Назва таблиці.
     * @param searchFields Список полів для WHERE-фільтра.
     * @param searchValues Список значень для WHERE-фільтра.
     * @return Кількість видалених рядків.
     */
    int delete(String tableName, List<String> searchFields, List<Object> searchValues) throws SQLException;
}
