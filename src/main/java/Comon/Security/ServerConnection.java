package Comon.Security;

import java.net.http.WebSocket;
import java.security.PublicKey;

public interface ServerConnection {
    void send(byte[] data);
    void send(PublicKey publicKey);
    void send(boolean bool);
    void send(String str);
    byte[] receive();
    String receiveStr();
    boolean reciveBool();
    PublicKey recivePublicKey();
}
