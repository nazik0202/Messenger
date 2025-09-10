package Server.Security;

import java.security.PublicKey;

public interface Database {
    public byte[] readPassword(String login);
    public PublicKey readPK(String login);
    public void edit(byte[] data, byte[] newData);
    public void edit(byte[] data, byte[] newData, byte[] publicKey);
    public void write(byte[] data, byte password, byte[] publicKey);
    public void delete(byte[] data);
}
