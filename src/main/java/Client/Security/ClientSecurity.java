package Client.Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ClientSecurity {
    public byte[] encrypt(byte[] salt, String txt){
        byte[] data = txt.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);          // додаємо соль
            return digest.digest(data);   // хешуємо пароль + соль
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
