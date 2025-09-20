package Server.Security;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface ServerSecurity {
    byte[] generateSalt();
    byte[] encrypt(byte[] data, byte[] salt);
    boolean check(PrivateKey privateKeyServer,PublicKey publicKeyClient,byte[] encryptedTruePassword,byte[] encryptedTry );
}