package Model;

import util.ChatType;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<User> participants;
    private List<Message> messages;
    private String name;
    private int id;
    private ChatType type;

    public Chat() {
    }

    public Chat(List<User> participants, List<Message> messages, String name, int id, ChatType type) {
        this.participants = participants;
        this.messages = messages;
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public Chat(List<User> participants) {
        this.participants = participants;
        this.messages = new ArrayList<>();
    }

    public Chat(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ChatType getType() {
        return type;
    }
}
