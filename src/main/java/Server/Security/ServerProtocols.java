package Server.Security;

import Comon.Security.Protocols;
import Comon.Security.ServerConnection;

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
        boolean loginInvalid = true;
        while (loginInvalid) {


//          2. клієнт вводить логін та відправляє серверу
            login = sc.receiveStr();
//          3. сервер перевіряє чи нема що нема в бд запису с таким логіном
            byte[] answ = db.readSL(login);

//          4. сервер передає відповідь
            if(answ == null){
                loginInvalid = false;

            }
//          5. якщо є то клієнт переносится на крок 2 та видоми повідомлення
            sc.send(!loginInvalid);
        }

//          6. сервер генерує та відправляє сіль
        byte[] salt = ss.generateSalt();
        sc.send(salt);
//          7. клієнт придумує пароль, шифрує і відправляє на сервер
        byte[] password = sc.receive();
//          8.сервер зберігає: логін, пароль, сіль
        db.write(login,password,salt);
//
        return false;
    }

}
