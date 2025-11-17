package Client;

import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;

import java.net.URI;
import java.util.Scanner;//login:elorig password:7162


public class MainClient {
    public static void main(String[] args) throws Exception {
        WebSocketClientConnection connection = new WebSocketClientConnection(new URI("ws://localhost:8080"));
        connection.connectBlocking(); // підключення до сервера
        Scanner sc = new Scanner(System.in);
        ClientProtocols protocols = new ClientProtocols(connection, new ClientSecurity());
        System.out.println("Choose option");
        System.out.println("1:Registration");
        System.out.println("2:Authentication");
        byte answ = sc.nextByte();
        if (answ == 1){
            System.out.println(protocols.registration());
            System.exit(0);
        } else if (answ == 2) {
            System.out.println(protocols.authentication());
            System.out.println("end of auth");
            System.exit(0);
        }else{
            System.exit(0);
        }
    }

}