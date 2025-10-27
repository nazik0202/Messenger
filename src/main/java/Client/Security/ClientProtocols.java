package Client.Security;

import Comon.Security.Protocols;
import Comon.Security.ServerConnection;

import java.util.Scanner;

public class ClientProtocols implements Protocols {
    ServerConnection sc ;
    ClientSecurity cs;
    Scanner scanner;

    public ClientProtocols(WebSocketClientConnection connection, ClientSecurity simpleClientSecurity) {
        this.cs = simpleClientSecurity;
        this.sc = connection;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public boolean authentication() {
        boolean auth = false;
        sc.send("authentication");

        String login = scanner.nextLine();
        sc.send(login);
        byte[] salt = sc.receive();
        sc.send(cs.encrypt(salt,scanner.nextLine()));
        auth = sc.reciveBool();
        System.out.println(auth);
        return auth;
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
        sc.send("registration");
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
        byte[] password = cs.encrypt(salt,scanner.nextLine());
        sc.send(password);
//          8.сервер зберігає: логін, пароль, сіль

        return false;
    }
}
