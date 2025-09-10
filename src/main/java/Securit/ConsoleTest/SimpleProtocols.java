package Securit.ConsoleTest;

import Client.Security.ClientSecurity;
import Client.Security.ConsoleTest.SimpleClientSecurity;
import Coman.Security.LocalServerConnection;
import Coman.ServerConnection;
import Securit.*;
import Server.Security.SimpleServerSecurity;
import Server.Security.Protocols;

import java.security.KeyPair;
import java.util.Scanner;


public class SimpleProtocols implements Protocols {
    private Database db;
    private ServerSecurity ss;
    private ServerConnection sc;
    private ClientSecurity cs;


    public SimpleProtocols() {
        this.ss = new SimpleServerSecurity();
        this.sc = new LocalServerConnection();
        this.cs = new SimpleClientSecurity();
    }

    @Override
    public boolean authentication(String password, String login) {
        //Сервер генерує ключи
        KeyPair keys = ss.generateKeys();
        //сервер передає публічний ключ клієнту

        sc.send(keys.getPublic());
        //клієнт приймає публічний ключ
        byte[] pubkey = sc.receive();
        //клієнт шифрує пароль
        byte[] passsword = password.getBytes();
        cs.encrypt(passsword,keys.getPublic());
        //клієнт передає серверу свій логін і зашифрований пароль
        //сервер отримує логін і зашифрований пароль
        //сервер дістає з бази данних зашифрований правильний пароль (використовує логін)
        //сервер перевіряє пароль

        return false;
    }

    @Override
    public boolean authentication(String userData) {
        return false;
    }

    @Override
    public boolean restorePassword(String userData) {
        return false;
    }

    @Override
    public boolean changePassword(String userData) {
        return false;
    }


    @Override
    public boolean registration(String userData){
        //    Корситувач вводить логін і пароль
        Scanner scanner = new Scanner(System.in);
        String login, password;
        System.out.println("enter login");
        login = scanner.nextLine();
        //    Передати на сервер логіn
        sc.send(login.getBytes());
        //    Сервер перевіряє чи ще нема акаунта з таким же логіном і передає відпоідь
        byte[] answ =  db.read(login.getBytes());
        //    Якщо такого логіна ще нема, клєінт геннерує пару ключів
        if(answ == null){
            KeyPair kp = cs.generateKeys();
            //    Клієнт шифрує пароль (або застосовує HASH до пароля)
            System.out.println("enter password");
            byte[] enpass = cs.encrypt(scanner.nextLine().getBytes(),kp.getPublic());
            //    Клієнт передає зашифрований пароль і публічний ключ
            //    Сервер добавляє новий запис в базу даних (логін, зашифрованйи пароль, публічний ключ)
        }
        else{
            System.out.println("already exists");
        }
        return false;
    }
}
