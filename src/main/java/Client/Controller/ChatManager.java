package Client.Controller;

import Client.Model.Chat;
import Client.Model.Message;
import Client.Model.User;

public interface ChatManager {
    Message writeMessage(User writer, Chat chat);
    void sendMessage(Message message, Chat chat);
    void editMessage(User editor, Chat chat,Message message, String newText);
    void deleteMessage(User user, Chat chat, Message message);
    void replyMessage(User replier, Message original, Chat chat, Message reply);
    void resendMessage(Message original, User resender, Chat originalChat, Chat resendChat);
    void readMessage(Chat chat, Message message, User reader);
}
