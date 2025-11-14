package Server;

import Server.Security.SQLITEDATABASE;
import Server.Security.ServerProtocols;
import Server.Security.WebSocketServerConnection;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServer extends WebSocketServer {
    private final Map<WebSocket, WebSocketServerConnection> connections = new ConcurrentHashMap<>();
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
        connections.put(conn, new WebSocketServerConnection(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WebSocketServerConnection connection = connections.get(conn);
        if (connection == null) {
            System.out.println("No connection found for " + conn.getRemoteSocketAddress());
            return;
        }

        connection.onMessage(message);

        // Якщо клієнт надіслав "registration"
        if ("registration".equals(message)) {
            new Thread(() -> {
                ServerProtocols protocols = new ServerProtocols(connection);
                protocols.registration();
            }).start();
        } else if ("authentication".equals(message)) {
            new Thread(() -> {
                ServerProtocols protocols = new ServerProtocols(connection);
                protocols.authentication();
            }).start();
        }
    }

    /*
    public void recyrsiya(int num){
        num1 = num - 1
        recursiya(num1)
    }
     */

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
