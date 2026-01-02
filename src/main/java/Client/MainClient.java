package Client;

import Client.Controller.NetworkChatManager;
import Client.Gui.GuiClient;
import Client.Model.Chat;
import Client.Model.Message;
import Client.Model.User;
import Client.Security.ClientProtocols;
import Client.Security.ClientSecurity;
import Client.Security.WebSocketClientConnection;
import javafx.application.Application;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;//login:qwe password:1
                         //login:asd password:2
                         //login:zxc password:3
                         //login:rty password:4
                         //login:fgh password:5
//

public class MainClient {
//    private static void startChatSession(WebSocketClientConnection connection, Scanner sc) {
//        System.out.println("--- Chat Mode (Async) ---");
//        boolean running = true;
//
//        while (running) {
//            System.out.println("\nSelect action:");
//            System.out.println("1. Create Chat (by user login)");
//            System.out.println("2. Send Message (by chat ID)");
//            System.out.println("3. Exit");
//
//            String choice = sc.nextLine();
//
//            switch (choice) {
//                case "1" -> {
//                    System.out.print("Enter target user login: ");
//                    String targetLogin = sc.nextLine();
//                    // Manual JSON construction to avoid dependency on client side for now
//                    String json = String.format(
//                            "{\"type\": \"create_chat\", \"payload\": {\"target_login\": \"%s\"}}",
//                            targetLogin
//                    );
//                    connection.send(json);
//                }
//                case "2" -> {
//                    System.out.print("Enter Chat ID: ");
//                    int chatId = Integer.parseInt(sc.nextLine());
//                    System.out.print("Enter Message: ");
//                    String content = sc.nextLine();
//
//                    // Server expects Base64 content
//                    String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
//
//                    String json = String.format(
//                            "{\"type\": \"send_message\", \"payload\": {\"chat_id\": %d, \"content\": \"%s\"}}",
//                            chatId, encodedContent
//                    );
//                    connection.send(json);
//                }
//                case "3" -> {
//                    running = false;
//                    System.out.println("Exiting...");
//                    System.exit(0);
//                }
//                default -> System.out.println("Unknown option");
//            }
//        }
//    }

    public static void main(String[] args) {
        // Просто викликає метод launch з GuiClient
        Application.launch(GuiClient.class, args);
    }

    private static void mainMenu(NetworkChatManager manager, Scanner sc, User me) {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. List My Chats");
            System.out.println("2. Create New Chat");
            System.out.println("3. Exit");
            System.out.print("Select: ");

            String choice = sc.nextLine();

            if (choice.equals("1")) {
                handleListChats(manager, sc, me);
            } else if (choice.equals("2")) {
                System.out.print("Enter user login: ");
                String login = sc.nextLine();
                manager.createChat(login);
            } else if (choice.equals("3")){
                System.exit(0);
            }
        }
    }

    private static void handleListChats(NetworkChatManager manager, Scanner sc, User me) {
        // 1. Fetch Chat Models
        List<Chat> chats = manager.fetchChats();

        if (chats.isEmpty()) {
            System.out.println("No chats found.");
            return;
        }

        System.out.println("\n--- Available Chats ---");
        for (int i = 0; i < chats.size(); i++) {
            Chat c = chats.get(i);
            System.out.printf("%d. %s [%s]%n", (i + 1), c.getName(), c.getType());
        }

        System.out.print("\nEnter number (0 to back): ");
        try {
            int selection = Integer.parseInt(sc.nextLine());
            if (selection > 0 && selection <= chats.size()) {
                Chat selectedChat = chats.get(selection - 1);
                chatView(manager, sc, selectedChat, me);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        }
    }
    private static void chatView(NetworkChatManager manager, Scanner sc, Chat chat, User me) {
        boolean inChat = true;

        // Load initial history into the Chat model
        manager.updateChatHistory(chat, 0);
        printMessages(chat);

        while (inChat) {
            System.out.println("\n[1] Write  [2] Refresh/Load  [3] Back");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    Message msg = manager.writeMessage(me, chat);
                    if (msg != null) {
                        manager.sendMessage(msg, chat);
                        try { Thread.sleep(100); } catch (Exception e){} // sync delay
                        manager.updateChatHistory(chat, 0); // refresh
                        printMessages(chat);
                    }
                    break;
                case "2":
                    // For demo, just reloading last 5. Logic for pagination can be added here.
                    manager.updateChatHistory(chat, 0);
                    printMessages(chat);
                    break;
                case "3":
                    inChat = false;
                    break;
            }
        }
    }
    private static void printMessages(Chat chat) {
        System.out.println("\n--- " + chat.getName() + " ---");
        for (Message m : chat.getMessages()) {
            System.out.printf("[%s] %s: %s%n",
                    m.getSentTime().toLocalTime(),
                    m.getSender().getNickName(),
                    m.getText());
        }
        System.out.println("--------------------");
    }


}