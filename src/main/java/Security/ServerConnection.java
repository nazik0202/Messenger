package Security;

import java.security.PublicKey;

public interface ServerConnection {
    void send(byte[] data);
    void send(PublicKey publicKey);
    byte[] receive();
    PublicKey recivePublicKey();
}
