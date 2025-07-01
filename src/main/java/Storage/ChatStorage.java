package Storage;

import Model.Chat;
import Model.Message;
import java.util.List;
import java.util.Optional;

public interface ChatStorage {

    /**
     * Save entire chat with all messages and metadata.
     */
    void saveChat(Chat chat);

    /**
     * Load chat by its unique ID.
     */
//    Optional<Chat> loadChatById(String chatId);

    /**
     * Save a single message in the context of a specific chat.
     */
    void saveMessage(Chat chat, Message message);

    /**
     * Load all messages from a specific chat.
     */
    List<Message> loadMessages(Chat chat);

    /**
     * Delete a message from a specific chat.
     */
    void deleteMessage(Chat chat, Message message);

    /**
     * Update an existing message (e.g. after editing).
     */
    void updateMessage(Chat chat, Message message);
}