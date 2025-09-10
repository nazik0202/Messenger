package Coman.Security;

import Coman.ServerConnection;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class LocalServerConnection implements ServerConnection {
    private byte[] buffer;
    @Override
    public void send(byte[] data) { this.buffer = data;}


    @Override
    public void send(PublicKey publicKey) {
        byte[] data = publicKey.getEncoded();
        this.buffer = data;
    }

    @Override
    public byte[] receive() { return buffer; }
    public String receiveStr() { return buffer.toString();}

    @Override
    public PublicKey recivePublicKey() {
        byte[] data = buffer;
        PublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return publicKey;
    }


}
