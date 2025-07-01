import Controller.ConsoleChatManager;
import Model.Chat;
import Model.User;
import Storage.FileChatStorage;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        FileChatStorage fileChatStorage = new FileChatStorage( "C:\\Users\\nazar\\IdeaProjects\\messenger\\data\\Chats");
        Chat coolChat = new Chat(1,"coolChat");
//        fileChatStorage.saveChat(coolChat);
        System.out.println(fileChatStorage.loadMessages(coolChat));

    }
}