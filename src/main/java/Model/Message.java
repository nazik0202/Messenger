package Model;

import util.MessageStatus;

import java.time.LocalDateTime;

public class Message {
    private MessageStatus status;
    private LocalDateTime sentTime;
    private LocalDateTime deliveredTime;
    private String text;
    private User sender;
    private byte[] media;
    private boolean edit;

    public Message(User sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public Message() {
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public String getText() {
        return text;
    }

    public User getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender.getNickName() +
                ", text='" + text + '\'' +
                ", deliveredTime=" + deliveredTime +
                ", status=" + status +
                "}\n";
    }
}
