package Controller;


import Model.Chat;
import Model.Message;
import Model.User;
import util.MessageStatus;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsoleChatManager implements ChatManager{
    @Override
    public Message writeMessage(User writer, Chat chat){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter message");
        String txtMessage = sc.nextLine();
        Message message = new Message(writer,txtMessage);
        return message;
    }
    @Override
    public void sendMessage(Message message, Chat chat){
        message.setStatus(MessageStatus.SENDING);
        message.setSentTime(LocalDateTime.now());
        System.out.print(message.getSender().getNickName()+": "+message.getText()+"\n send time:["+message.getSentTime()+"] *");
        message.setStatus(MessageStatus.DELIVERED_CLIENT);
        message.setDeliveredTime(LocalDateTime.now());
        System.out.println("* \n delivered time:["+message.getDeliveredTime()+"]");


    }
    @Override
    public void editMessage(User editor, Chat chat,Message message, String newText){

    }
    @Override
    public void deleteMessage(User user, Chat chat, Message message){

    }
    @Override
    public void replyMessage(User replier, Message original, Chat chat, Message reply) {

    }
    @Override
    public void resendMessage(Message original, User resender, Chat originalChat, Chat resendChat){

    }
    @Override
    public void readMessage(Chat chat, Message message, User reader){

    }

}
