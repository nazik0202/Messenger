package Client.Model;

import Client.util.MessageStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

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
        this.edit = false;
    }

    public Message() {
    }

    public void setSender(User sender) {
        this.sender = sender;
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

    public void setText(String text) {
        this.text = text;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
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

    public boolean isEdit() {
        return edit;
    }

    @Override
    public String toString() {
        return  sender.getNickName() + "\n"+
                text + '\n' +
                "dT=" + deliveredTime +
                "  status=" + status + "/n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return edit == message.edit && status == message.status && Objects.equals(sentTime, message.sentTime) && Objects.equals(deliveredTime, message.deliveredTime) && Objects.equals(text, message.text) && Objects.equals(sender, message.sender) && Objects.deepEquals(media, message.media);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, sentTime, deliveredTime, text, sender, Arrays.hashCode(media), edit);
    }
}
