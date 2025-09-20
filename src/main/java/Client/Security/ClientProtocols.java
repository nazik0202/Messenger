package Client.Security;

import Comon.Security.LocalServerConnection;
import Comon.Security.Protocols;
import Comon.Security.ServerConnection;
import Server.Security.ServerProtocols;

import java.util.Scanner;

public class ClientProtocols implements Protocols {
    ServerConnection sc ;
    ClientSecurity cs;
    Scanner scanner;

    public ClientProtocols() {
        this.cs = new SimpleClientSecurity();
        this.sc = new LocalServerConnection();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public boolean authentication() {
        return false;
    }

    @Override
    public boolean restorePassword() {
        return false;
    }

    @Override
    public boolean changePassword() {
        return false;
    }

    @Override
    public boolean registration() {
        boolean loginValid = false;
//          1. клієнт викликає протокол регестрації
        ServerProtocols sp = new ServerProtocols(sc);
        sp.registration();
        while (!loginValid) {
//          2. клієнт вводить логін та відправляє серверу
            String login = scanner.nextLine();
            sc.send(login);
//          3. сервер перевіряє чи нема що нема в бд запису с таким логіном
//          4. сервер передає відповідь
            loginValid = sc.reciveBool();
//          5. якщо є то клієнт переносится на крок 2 та видоми повідомлення
        }
//          6. сервер генерує та відправляє сіль
        byte[] salt = sc.receive();
//          7. клієнт придумує пароль, шифрує і відправляє на сервер
        byte[] password = null;// я  не знаю як зашифрувати
        sc.send(password);
//          8.сервер зберігає: логін, пароль, сіль

        return false;
    }
}
