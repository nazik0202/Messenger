package Server.DataBase.Classes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
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
    public JSONArray getUserChatsWithDetails(int userId) {
        String sql = """
            SELECT c.id, c.is_group_chat, c.chat_name, u.login as other_user_login
            FROM chats c
            JOIN chat_users cu_me ON c.id = cu_me.chat_id
            LEFT JOIN chat_users cu_other ON c.id = cu_other.chat_id AND cu_other.user_id != ?
            LEFT JOIN users u ON cu_other.user_id = u.id
            WHERE cu_me.user_id = ?
        """;

        JSONArray chats = new JSONArray();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject chat = new JSONObject();
                chat.put("id", rs.getInt("id"));
                boolean isGroup = rs.getBoolean("is_group_chat");

                // Логіка відображення назви
                if (isGroup) {
                    chat.put("name", rs.getString("chat_name"));
                    chat.put("type", "group");
                } else {
                    String otherLogin = rs.getString("other_user_login");
                    chat.put("name", otherLogin != null ? otherLogin : "Unknown User");
                    chat.put("type", "private");
                }
                chats.put(chat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

}
