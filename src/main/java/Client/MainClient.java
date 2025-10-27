package Client;

import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;

import java.net.URI;


public class MainClient {
    public static void main(String[] args) throws Exception {
        WebSocketClientConnection connection = new WebSocketClientConnection(new URI("ws://localhost:8080"));
        connection.connectBlocking(); // підключення до сервера

        ClientProtocols protocols = new ClientProtocols(connection, new ClientSecurity());
        protocols.registration();
        protocols.authentication();
        System.out.println("end of auth");

    }
}