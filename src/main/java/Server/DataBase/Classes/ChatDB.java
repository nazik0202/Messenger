package Server.DataBase.Classes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatDB extends SQLiteDataBase {

    public ChatDB() {
        super(DatabaseSchema.Table.CHATS);

        SQLiteDataBase sqlDBcu = new SQLiteDataBase(DatabaseSchema.Table.CHAT_USERS);// Ensures tables exist
        SQLiteDataBase sqlDBm = new SQLiteDataBase(DatabaseSchema.Table.MESSAGES);
    }

    // Create a private chat between two users
    public long createPrivateChat(int userId1, int userId2) {
        try {
            System.out.println("Create private chat");
            // 1. Create chat entry
            long chatId = write(DatabaseSchema.Table.CHATS.getTableName(),
                    List.of("is_group_chat"),
                    List.of(false));

            // 2. Add both users to the chat
            write(DatabaseSchema.Table.CHAT_USERS.getTableName(),
                    List.of("user_id", "chat_id"),
                    List.of(userId1, chatId));

            write(DatabaseSchema.Table.CHAT_USERS.getTableName(),
                    List.of("user_id", "chat_id"),
                    List.of(userId2, chatId));

            return chatId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create chat", e);
        }
    }

    // Get all chats for a specific user
    public List<Map<String, Object>> getUserChats(int userId) {
        // This requires a JOIN, so we use a raw query helper or implement a specific method
        // For simplicity using raw SQL here as generic 'read' doesn't support JOINs well yet
        String sql = """
            SELECT c.id, c.chat_name, c.is_group_chat 
            FROM chats c
            JOIN chat_users cu ON c.id = cu.chat_id
            WHERE cu.user_id = ?
        """;
        // In a real scenario, you'd extend SQLiteDataBase to handle custom queries
        // or use the generic read on 'chat_users' and then loop (inefficient but works for prototype)
        return new ArrayList<>(); // Placeholder for logic
    }

    public void saveMessage(int chatId, int senderId, byte[] content) {
        try {
            write(DatabaseSchema.Table.MESSAGES.getTableName(),
                    List.of("chat_id", "sender_id", "content"),
                    List.of(chatId, senderId, content));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserIdByLogin(String login) {
        try {
            var res = read(DatabaseSchema.Table.USERS.getTableName(),
                    List.of("id"),
                    List.of("login"),
                    List.of(login));
            if (res.isEmpty()) return -1;
            System.out.println("userId:"+(Integer) res.get(0).get("id"));
            return (Integer) res.get(0).get("id");
        } catch (SQLException e) {
            return -1;
        }
    }
}
