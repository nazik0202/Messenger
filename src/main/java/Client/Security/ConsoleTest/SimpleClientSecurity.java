package Client.Security.ConsoleTest;

import Client.Security.ClientSecurity;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SimpleClientSecurity implements ClientSecurity {
    @Override
    public KeyPair generateKeys() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            return kp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] data, PublicKey publicKey) {
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);

        }catch(Exception e){
            throw new RuntimeException(e);
        }



    }

    @Override
    public byte[] decrypt(byte[] data, PrivateKey privateKey) {
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
