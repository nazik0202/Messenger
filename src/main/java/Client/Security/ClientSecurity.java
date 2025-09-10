package Client.Security;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface ClientSecurity {
    KeyPair generateKeys();
    byte[] encrypt(byte[] data, PublicKey publicKey);
    byte[] decrypt(byte[] data, PrivateKey privateKey);
}
