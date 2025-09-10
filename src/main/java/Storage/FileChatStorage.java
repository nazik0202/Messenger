package Storage;

import Client.Model.Chat;
import Client.Model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class FileChatStorage implements ChatStorage {

    private final String baseStoragePath;
    private final ObjectMapper objectMapper;


    public FileChatStorage(String baseStoragePath) {
        this.baseStoragePath = baseStoragePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Для красивого форматування JSON
        initializeStorageDirectory();

        objectMapper.registerModule(new JavaTimeModule());
    }

    private void initializeStorageDirectory() {
        Path path = Paths.get(baseStoragePath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Created base storage directory: " + baseStoragePath);
            } catch (IOException e) {
                System.err.println("Failed to create base storage directory: " + baseStoragePath + " Error: " + e.getMessage());
            }
        }
    }

    private Path getChatFilePath(Chat chat) {
        return Paths.get(baseStoragePath, chat.getId() + ".json");
    }

    public Chat loadChatFromFile(Path chatFilePath) {
        try {
            if (Files.exists(chatFilePath)) {
                return objectMapper.readValue(chatFilePath.toFile(), Chat.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading chat from file " + chatFilePath + ": " + e.getMessage());
        }
        return null;
    }

    private void saveChatToFile(Chat chat, Path chatFilePath) {
        try {
            objectMapper.writeValue(chatFilePath.toFile(), chat);
        } catch (IOException e) {
            System.err.println("Error saving chat to file " + chatFilePath + ": " + e.getMessage());
        }
    }

    @Override
    public void saveChat(Chat chat) {
        if (chat == null) {
            System.err.println("Cannot save null chat.");
            return;
        }
        Path chatFilePath = getChatFilePath(chat);
        saveChatToFile(chat, chatFilePath);
        System.out.println("Chat saved: " + chat.getId());
    }

    // Закоментований метод з інтерфейсу, який можна додати
    // @Override
    // public Optional<Chat> loadChatById(String chatId) {
    //     Path chatFilePath = Paths.get(baseStoragePath, chatId + ".json");
    //     Chat chat = loadChatFromFile(chatFilePath);
    //     return Optional.ofNullable(chat);
    // }

    @Override
    public void saveMessage(Chat chat, Message message) {
        if (chat == null || message == null) {
            System.err.println("Cannot save message for null chat or null message.");
            return;
        }
        Path chatFilePath = getChatFilePath(chat);
        Chat existingChat = loadChatFromFile(chatFilePath);

        if (existingChat == null) {
            // Якщо чат ще не існує у файлі, створюємо новий чат з цим повідомленням
            // Або ви можете вирішити кинути виняток, якщо очікується, що чат вже збережено
            existingChat = new Chat(chat.getId(), chat.getName()); // Припустимо, що Chat має конструктор для ID та імені
            if (existingChat.getMessages() == null) {
                existingChat.setMessages(new ArrayList<>());
            }
        }

        if (existingChat.getMessages() == null) {
            existingChat.setMessages(new ArrayList<>());
        }
        existingChat.getMessages().add(message);
        saveChatToFile(existingChat, chatFilePath);
        System.out.println("Message saved to chat " + chat.getId() + ": " + message.getText());
    }

    @Override
    public List<Message> loadMessages(Chat chat) {
        if (chat == null) {
            System.err.println("Cannot load messages for null chat.");
            return Collections.emptyList();
        }
        Path chatFilePath = getChatFilePath(chat);
        Chat existingChat = loadChatFromFile(chatFilePath);
        return (existingChat != null && existingChat.getMessages() != null) ? existingChat.getMessages() : Collections.emptyList();
    }

    @Override
    public void deleteMessage(Chat chat, Message message) {
        if (chat == null || message == null) {
            System.err.println("Cannot delete message for null chat or null message.");
            return;
        }
        Path chatFilePath = getChatFilePath(chat);
        Chat existingChat = loadChatFromFile(chatFilePath);

        if (existingChat != null && existingChat.getMessages() != null) {
            boolean removed = existingChat.getMessages().removeIf(msg -> msg.equals(message)); // Використовуйте equals коректно
            if (removed) {
                saveChatToFile(existingChat, chatFilePath);
                System.out.println("Message deleted from chat " + chat.getId() + ": " + message.getText());
            } else {
                System.out.println("Message not found in chat " + chat.getId() + " for deletion.");
            }
        } else {
            System.out.println("Chat " + chat.getId() + " not found or has no messages to delete from.");
        }
    }

    @Override
    public void updateMessage(Chat chat, Message message) {
        if (chat == null || message == null) {
            System.err.println("Cannot update message for null chat or null message.");
            return;
        }
        Path chatFilePath = getChatFilePath(chat);
        Chat existingChat = loadChatFromFile(chatFilePath);

        if (existingChat != null && existingChat.getMessages() != null) {
            boolean updated = false;
            List<Message> updatedMessages = new ArrayList<>();
            for (Message msg : existingChat.getMessages()) {
                if (msg.equals(message)) { // Припускаємо, що Message має коректний метод equals
                    updatedMessages.add(message); // Замінюємо старе повідомлення на оновлене
                    updated = true;
                } else {
                    updatedMessages.add(msg);
                }
            }
            if (updated) {
                existingChat.setMessages(updatedMessages);
                saveChatToFile(existingChat, chatFilePath);
                System.out.println("Message updated in chat " + chat.getId() + ": " + message.getText());
            } else {
                System.out.println("Message not found in chat " + chat.getId() + " for update.");
            }
        } else {
            System.out.println("Chat " + chat.getId() + " not found or has no messages to update.");
        }
    }
}