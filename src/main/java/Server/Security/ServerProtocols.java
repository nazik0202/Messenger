package Server.Security;

import Comon.Security.Protocols;
import Comon.Security.ServerConnection;

import java.util.Base64;

public class ServerProtocols implements Protocols {
    ServerConnection sc;
    Database db;
    ServerSecurity ss;

    public ServerProtocols(ServerConnection sc) {
        this.sc = sc;
        this.db = new SQLITEDATABASE();
        this.ss = new SimpleServerSecurity(sc,db);
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
//          1. клієнт викликає протокол регестраці
        String login = "";
        boolean loginValid = false;
        String temp = sc.receiveStr();
        while (!loginValid) {
            System.out.println("server is waiting for login");

//          2. клієнт вводить логін та відправляє серверу
            login = sc.receiveStr();
            System.out.println("server received login:"+login);
//          3. сервер перевіряє чи нема що нема в бд запису с таким логіном
            byte[] answ = db.readSL(login);

//          4. сервер передає відповідь
            if(answ == null){
                loginValid = true;
                System.out.println("login is valid");
            }
//          5. якщо є то клієнт переносится на крок 2 та видоми повідомлення
            sc.send(loginValid);
        }

//          6. сервер генерує та відправляє сіль
        byte[] salt = ss.generateSalt();
        sc.send(salt);
//          7. клієнт придумує пароль, шифрує і відправляє на сервер
        byte[] password = sc.receive();

//          8.сервер зберігає: логін, пароль, сіль
        db.write(login,password,salt);
//
        return true;
    }

}
