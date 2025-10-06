package Server;

import Server.Security.SQLITEDATABASE;
import Server.Security.ServerProtocols;
import Server.Security.WebSocketServerConnection;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MainServer extends WebSocketServer {
    public static void main(String[] args){
        MainServer server = new MainServer(8080);
        server.start();
    }

    public MainServer(int port) {
        super(new InetSocketAddress(port));
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WebSocketServerConnection connection = new WebSocketServerConnection(conn);
        connection.onMessage(message);

        // Якщо клієнт надіслав "registration"
        if ("registration".equals(message)) {
            ServerProtocols protocols = new ServerProtocols(connection);
            protocols.registration();
        }
        else if ("authentication".equals(message)) {
            ServerProtocols protocols = new ServerProtocols(connection);
            protocols.authentication();
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("Exeption:"+e);
    }

    @Override
    public void onStart() {
        System.out.println("Server started");
    }
}
