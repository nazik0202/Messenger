package Client.Controller;

import Client.Model.Chat;
import Client.Model.Message;
import Client.Model.User;
import Client.Security.WebSocketClientConnection;
import Client.util.ChatType;
import Client.util.MessageStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class NetworkChatManager implements ChatManager {
    private final WebSocketClientConnection connection;
    private final User currentUser;

    public NetworkChatManager(WebSocketClientConnection connection, User currentUser) {
        this.connection = connection;
        this.currentUser = currentUser;
    }

    // --- Interface Methods ---

    @Override
    public Message writeMessage(User writer, Chat chat) {
        Scanner sc = new Scanner(System.in);
        System.out.print("You: ");
        String text = sc.nextLine();
        if (text.isEmpty()) return null;
        return new Message(writer, text);
    }

    @Override
    public void sendMessage(Message message, Chat chat) {
        if (message == null) return;

        String encodedContent = Base64.getEncoder().encodeToString(message.getText().getBytes());
        String json = String.format(
                "{\"type\": \"send_message\", \"payload\": {\"chat_id\": %d, \"content\": \"%s\"}}",
                chat.getId(), encodedContent
        );
        connection.send(json);
    }

    @Override
    public void editMessage(User editor, Chat chat, Message message, String newText) {
        // TODO: Implement on server
    }

    @Override
    public void deleteMessage(User user, Chat chat, Message message) {
        // TODO: Implement on server
    }

    @Override
    public void replyMessage(User replier, Message original, Chat chat, Message reply) {
        // TODO: Implement on server
    }

    @Override
    public void resendMessage(Message original, User resender, Chat originalChat, Chat resendChat) {
        // TODO: Implement logic
    }

    @Override
    public void readMessage(Chat chat, Message message, User reader) {
        // Server marks as read automatically on get_history
    }

    // --- Network Specific Methods (Fetching Data) ---

    public List<Chat> fetchChats() {
        connection.send("{\"type\": \"get_chats\"}");
        String response = connection.receiveStr();

        JSONObject json = new JSONObject(response);
        List<Chat> chats = new ArrayList<>();

        if (json.getString("type").equals("chats_list")) {
            JSONArray array = json.getJSONArray("payload");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                String typeStr = obj.optString("type", "private");

                ChatType type = typeStr.equals("group") ? ChatType.GROUP : ChatType.PRIVATE;
                chats.add(new Chat(new ArrayList<>(), new ArrayList<>(), name, id, type));
            }
        }
        return chats;
    }

    public void createChat(String targetLogin) {
        String json = String.format("{\"type\": \"create_chat\", \"payload\": {\"target_login\": \"%s\"}}", targetLogin);
        connection.send(json);
        // Server sends confirmation, MainClient loop handles sync usually,
        // but here we just consume the response to keep flow clean
        String response = connection.receiveStr();
        System.out.println("Server: " + response);
    }

    public void sendReadStatus(Message msg){
        String json = String.format("{\"type\": \"read_status\", \"payload\": {\"id\": %d, \"status\": \"read\"}}", msg.getId());
        connection.send(json);
        String response = connection.receiveStr();
        System.out.println("Server: " + response);
    }

    public void updateChatHistory(Chat chat, int offset) {
        String json = String.format("{\"type\": \"get_history\", \"payload\": {\"chat_id\": %d, \"offset\": %d}}", chat.getId(), offset);
        connection.send(json);

        String response = connection.receiveStr();
        JSONObject responseJson = new JSONObject(response);


        if (responseJson.getString("type").equals("chat_history")) {
            JSONArray messagesArray = responseJson.getJSONArray("payload");
            List<Message> newMessages = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject msgObj = messagesArray.getJSONObject(i);

                // Map JSON to User
                User sender = new User(msgObj.getInt("sender_id"), msgObj.getString("sender"));

                // Map JSON to Message
                Message msg = new Message(sender, msgObj.getString("content"));
                msg.setId(msgObj.getInt("id"));
                // Parse timestamp (Simple approach, assuming server sends readable format)
                try {
                    String timeStr = msgObj.getString("timestamp");
                    msg.setSentTime(LocalDateTime.parse(timeStr, formatter));
                } catch (Exception e) {
                    msg.setSentTime(LocalDateTime.now());
                }

                String statusStr = msgObj.optString("status", "sent");
                msg.setStatus(mapStatus(statusStr));

                newMessages.add(msg);
            }
            // Update model
            if (offset == 0) {
                chat.setMessages(newMessages);
            } else {
                // Prepend or append depending on UI logic, here we just set for simplicity
                chat.getMessages().addAll(0, newMessages);
            }
        }
    }

    private MessageStatus mapStatus(String status) {
        return switch (status) {
            case "read" -> MessageStatus.READ;
            case "delivered_server" -> MessageStatus.DELIVERED_SERVER;
            default -> MessageStatus.SENDING;
        };
    }


}
