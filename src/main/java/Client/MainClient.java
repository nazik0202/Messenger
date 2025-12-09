package Client;

import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;

import java.net.URI;
import java.util.Base64;
import java.util.Scanner;//login:qwe password:1
                         //login:asd password:2


public class MainClient {
    private static void startChatSession(WebSocketClientConnection connection, Scanner sc) {
        System.out.println("--- Chat Mode (Async) ---");
        boolean running = true;

        while (running) {
            System.out.println("\nSelect action:");
            System.out.println("1. Create Chat (by user login)");
            System.out.println("2. Send Message (by chat ID)");
            System.out.println("3. Exit");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter target user login: ");
                    String targetLogin = sc.nextLine();
                    // Manual JSON construction to avoid dependency on client side for now
                    String json = String.format(
                            "{\"type\": \"create_chat\", \"payload\": {\"target_login\": \"%s\"}}",
                            targetLogin
                    );
                    connection.send(json);
                }
                case "2" -> {
                    System.out.print("Enter Chat ID: ");
                    int chatId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter Message: ");
                    String content = sc.nextLine();

                    // Server expects Base64 content
                    String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());

                    String json = String.format(
                            "{\"type\": \"send_message\", \"payload\": {\"chat_id\": %d, \"content\": \"%s\"}}",
                            chatId, encodedContent
                    );
                    connection.send(json);
                }
                case "3" -> {
                    running = false;
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Unknown option");
            }
        }
    }

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
            boolean auth = protocols.authentication();
            System.out.println(auth);
            System.out.println("end of auth");
            if(auth){
                startChatSession(connection,sc);
            }
            else {

            }
            System.exit(0);
        }else{
            System.exit(0);
        }
    }

}