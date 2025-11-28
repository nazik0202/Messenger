package Server.Connection;

import Server.Connection.WebSocketServerConnection;
import Server.DataBase.Classes.ChatDB;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
                case "create_chat":
                    handleCreateChat(json, connection);
                    break;
                case "send_message":
                    handleSendMessage(json, connection);
                    break;
                // Add cases for "get_chats", "get_history" etc.
                default:
                    System.out.println("Unknown command: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error handling async message: " + e.getMessage());
        }
    }

    private void handleCreateChat(JSONObject json, WebSocketServerConnection connection) {
        String targetLogin = json.getJSONObject("payload").getString("target_login");
        int targetId = chatDB.getUserIdByLogin(targetLogin);

        if (targetId != -1) {
            long chatId = chatDB.createPrivateChat(connection.getUserId(), targetId);
            connection.send("{\"type\": \"chat_created\", \"chatId\": " + chatId + "}");
        } else {
            connection.send("{\"type\": \"error\", \"msg\": \"User not found\"}");
        }
    }

    private void handleSendMessage(JSONObject json, WebSocketServerConnection connection) {
        JSONObject payload = json.getJSONObject("payload");
        int chatId = payload.getInt("chat_id");
        String contentBase64 = payload.getString("content");
        byte[] content = Base64.getDecoder().decode(contentBase64);

        chatDB.saveMessage(chatId, connection.getUserId(), content);
        // Here logic to find the other user in the chat and send message via WebSocket
    }
}
