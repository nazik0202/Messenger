package Security;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface ServerSecurity {
    KeyPair generateKeys();
    byte[] encrypt(byte[] data, PublicKey publicKey);
    byte[] decrypt(byte[] data, PrivateKey privateKey);
    boolean check(PrivateKey privateKeyServer,PublicKey publicKeyClient,byte[] encryptedTruePassword,byte[] encryptedTry );
}
