package Server.Security;

public interface Protocols {

    boolean authentication(String password,String login);

    boolean authentication(String userData);

    boolean restorePassword(String userData);

    boolean changePassword(String userData);

    boolean registration(String userData);
}