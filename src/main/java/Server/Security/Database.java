package Server.Security;

import java.security.PublicKey;

public interface Database {
    public byte[] readPassword(String login);
    public byte[] readSL(String login);
    public void edit(String login, byte[] newData);
    public void edit(String login, byte[] newData, byte[] salt);
    public void write(String login, byte[] password, byte[] salt);
    public void delete(String login);
}
