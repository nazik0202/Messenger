import Controller.ConsoleChatManager;
import Model.Chat;
import Model.Message;
import Model.User;
import Security.ConsoleTest.SimpleServerSecurity;
import Storage.FileChatStorage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SimpleServerSecurity sss = new SimpleServerSecurity();
        sss.generateKeys();
        
        
        
        
//

//        User coolUser0 = new User(0,"coolUser0");
//        User coolUser1 = new User(1,"coolUser1");
//
//        List<User> users = new ArrayList<>();
//        users.add(coolUser0);
//        users.add(coolUser1);
////
//        Chat coolChat = new Chat(2,"coolChat");
////
//        coolChat.setParticipants(users);
//        Message message;
//        for  (User user: users){
//            message = cManager.writeMessage(user,coolChat);
//            cManager.sendMessage(message,coolChat);
//
//        }
//
//        fileChatStorage.saveChat(coolChat);

//        coolChat.setParticipants(users);
//
//        cManager.sendMessage(cManager.writeMessage(coolUser0,coolChat),coolChat);
//
//        cManager.sendMessage(cManager.writeMessage(coolUser1,coolChat),coolChat);
    }
}