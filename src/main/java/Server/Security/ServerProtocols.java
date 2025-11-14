package Server.Security;

import Comon.Security.Protocols;
import Comon.Security.ServerConnection;
import org.sqlite.SQLiteException;

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
        /*
        1.клієнт визиває протокол аунтефікації
        2.сервер запускаєаунтефікацію
        3.клієнт вводить логін
        4.сервер шукає цей логін та якщо є то відправяє сіль
        5.клієнт вводить пароль та шифрує і відправляє на сервер
        6.сервер перевіряє пароль та якщо првалильно то впускає користуувача
         */
        String temp = sc.receiveStr();
        System.out.println("server is waiting for login");
        String login = sc.receiveStr();
        sc.send(db.readSL(login));
        System.out.println("server is waiting for pasword");
        byte[] password = sc.receive();
        byte[] pfdb = db.readPassword(login);
        String passwordS =  Base64.getEncoder().encodeToString(password);
        String pfdbS =  Base64.getEncoder().encodeToString(pfdb);
        boolean auth = passwordS.equals(pfdbS);
        sc.send(auth);
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
        try {
            db.write(login,password,salt);
        }catch (RuntimeException e){
            sc.send(false);
            return false;
        }
        sc.send(true);
//
        return true;
    }

}
