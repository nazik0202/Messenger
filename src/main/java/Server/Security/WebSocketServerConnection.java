package Server.Security;

import Comon.Security.ServerConnection;
import org.java_websocket.WebSocket;

import java.security.PublicKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketServerConnection implements ServerConnection {

    private final WebSocket socket;
    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>();

    public WebSocketServerConnection(WebSocket socket) {
        this.socket = socket;
    }

    // Викликається серверним WebSocketServer при отриманні повідомлення
    public void onMessage(String message) {
        incoming.offer(message);}

    @Override
    public void send(byte[] data) {
        socket.send(data);
    }

    @Override
    public void send(PublicKey publicKey) {

    }

    @Override
    public void send(boolean bool) {
        socket.send(Boolean.toString(bool));
    }

    @Override
    public void send(String str) {
        socket.send(str);
        System.out.println(str+" sent");
    }

    @Override
    public byte[] receive() {
        try {
            // Забираємо наступний рядок і перетворюємо в байти
            String msg = incoming.take();
            incoming.clear(); // очищаємо, щоб старі повідомлення не залишались
            return msg.getBytes();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String receiveStr() {
        try {
            String msg = incoming.take();
            incoming.clear();// очищаємо після прочитання
            System.out.println(msg+" received");
            return msg;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean reciveBool() {
        return Boolean.parseBoolean(receiveStr());
    }

    @Override
    public PublicKey recivePublicKey() {
        return null;
    }
}
