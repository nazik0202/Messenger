package Client.Security;

import Comon.Security.ServerConnection;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.PublicKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketClientConnection extends WebSocketClient implements ServerConnection {

    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>();

    public WebSocketClientConnection(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        incoming.offer(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed." + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    // ===== Implementation of ServerConnection =====
    @Override
    public void send(byte[] data) {
        super.send(data);
    }

    @Override
    public void send(PublicKey publicKey) {

    }

    @Override
    public void send(boolean bool) {
        super.send(Boolean.toString(bool));
    }

    @Override
    public void send(String str) {
        super.send(str);
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
            incoming.clear(); // очищаємо після прочитання
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
