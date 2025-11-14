package Server.Security;

import Comon.Security.ServerConnection;
import org.java_websocket.WebSocket;

import java.security.PublicKey;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketServerConnection implements ServerConnection {

    private final WebSocket socket;
    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>();
    private volatile boolean isAuthenticated = false;
    private volatile int userId = -1;

    public WebSocketServerConnection(WebSocket socket) {
        this.socket = socket;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public int getUserId() {
        return userId;
    }
    public void setAuthenticated(int userId) {
        this.userId = userId;
        this.isAuthenticated = true;
        incoming.clear();
    }


    // Викликається серверним WebSocketServer при отриманні повідомлення
    public void onMessage(String message) {
        System.out.println("new message: "+message);
        incoming.offer(message);
        System.out.println(incoming);
    }

    @Override
    public void send(byte[] data) {
        String base64 = Base64.getEncoder().encodeToString(data);
        socket.send(base64);

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
            return Base64.getDecoder().decode(msg);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String receiveStr() {
        try {
            String msg = "";
            for (int i = 0;i<30000;i++) {
                msg = incoming.take();
                if(msg != ""){
                    break;
                }
                else {
                    wait(100);
                    System.out.println(i);
                }
            }
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
