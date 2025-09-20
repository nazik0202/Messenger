package Server.Security;



import Comon.Security.ServerConnection;

import java.security.*;
import java.util.Arrays;

public class SimpleServerSecurity implements Server.Security.ServerSecurity {

    private byte[] publicKey;

    private ServerConnection connection;
    private Database db;

    public SimpleServerSecurity(ServerConnection connection, Database db){
        this.connection = connection;
        this.db = db;
    }


    @Override
    public byte[] generateSalt() {
        try {
            // generate random salt (16 bytes)
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            // store it для подальшого використання (якщо треба)
            this.publicKey = salt; // замість this.publicKey

            // повертаємо "public key" як байти (salt)
            return salt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);          // додаємо соль
            return digest.digest(data);   // хешуємо пароль + соль
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean check(PrivateKey privateKeyServer, PublicKey publicKeyClient, byte[] encryptedTruePassword, byte[] encryptedTry) {
        return false;
    }

}
