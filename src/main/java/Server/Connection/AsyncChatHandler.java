package Server.Connection;

import Server.Connection.WebSocketServerConnection;
import Server.DataBase.Classes.ChatDB;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;

import java.sql.*;
import java.util.List;

public class AsyncChatHandler {

    private final ChatDB chatDB;

    public AsyncChatHandler() {
        this.chatDB = new ChatDB();
    }

    public void handle(String message, WebSocketServerConnection connection) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");

            switch (type) {
                case "get_chats":
                    handleGetChats(connection);
                    break;
                case "get_history":
                    handleGetHistory(json, connection);
                    break;
                case "create_chat":
                    handleCreateChat(json, connection);
                    break;
                case "send_message":
                    handleSendMessage(json, connection);
                    break;
                default:
                    System.out.println("Unknown command: " + type);

            } catch(Exception e){
                System.err.println("Error handling async message: " + e.getMessage());
            }
        }

        private void handleCreateChat (JSONObject json, WebSocketServerConnection connection){
            String targetLogin = json.getJSONObject("payload").getString("target_login");
            int targetId = chatDB.getUserIdByLogin(targetLogin);

            if (targetId != -1) {
                long chatId = chatDB.createPrivateChat(connection.getUserId(), targetId);
                connection.send("{\"type\": \"chat_created\", \"chatId\": " + chatId + "}");
            } else {
                connection.send("{\"type\": \"error\", \"msg\": \"User not found\"}");
            }
        }

        private void handleSendMessage (JSONObject json, WebSocketServerConnection connection){
            JSONObject payload = json.getJSONObject("payload");
            int chatId = payload.getInt("chat_id");
            String contentBase64 = payload.getString("content");
            byte[] content = Base64.getDecoder().decode(contentBase64);

            chatDB.saveMessage(chatId, connection.getUserId(), content);
            // Here logic to find the other user in the chat and send message via WebSocket
        }
    }

    private void handleSendMessage(JSONObject json, WebSocketServerConnection connection) {
    }

    private void handleGetChats(WebSocketServerConnection connection) {
        JSONArray chats = chatDB.getUserChatsWithDetails(connection.getUserId());
        JSONObject response = new JSONObject();
        response.put("type", "chats_list");
        response.put("payload", chats);
        connection.send(response.toString());
    }

    private void handleGetHistory(JSONObject json, WebSocketServerConnection connection) {
        int chatId = json.getJSONObject("payload").getInt("chat_id");
        int offset = json.getJSONObject("payload").optInt("offset", 0); // Скільки пропустити (для пагінації)
        int limit = 5; // Завантажуємо по 5

        // 1. Отримуємо повідомлення
        JSONArray messages = chatDB.getChatHistory(chatId, limit, offset);

        // 2. Відмічаємо як прочитані (бо користувач їх запитав)
        //chatDB.markMessagesAsRead(chatId, connection.getUserId());

        JSONObject response = new JSONObject();
        response.put("type", "chat_history");
        response.put("chat_id", chatId);
        response.put("payload", messages);
        connection.send(response.toString());
    }
    public JSONArray getChatHistory(int chatId, int limit, int offset) {
        String sql = """
            SELECT m.id, m.content, m.timestamp, m.status, m.sender_id, u.login
            FROM messages m
            JOIN users u ON m.sender_id = u.id
            WHERE m.chat_id = ?
            ORDER BY m.timestamp DESC
            LIMIT ? OFFSET ?
        """;

        JSONArray messages = new JSONArray();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chatId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject msg = new JSONObject();
                msg.put("id", rs.getInt("id"));
                msg.put("sender", rs.getString("login"));
                msg.put("sender_id", rs.getInt("sender_id"));
                // Контент в базі BLOB, але ми зберігали Base64 стрінгу як байти, тому просто читаємо
                msg.put("content", new String(rs.getBytes("content")));
                msg.put("timestamp", rs.getString("timestamp"));
                msg.put("status", rs.getString("status"));
                messages.put(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Реверс, щоб на клієнті показувати хронологічно (знизу нові)
        JSONArray reversed = new JSONArray();
        for (int i = messages.length() - 1; i >= 0; i--) {
            reversed.put(messages.get(i));
        }
        return reversed;
    }

}

