package ru.tsindrenko;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Admin extends User {

    private ChatRoom chatRoom;

    Admin(){
    }

    Admin(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    public synchronized void banUser(User user){
        chatRoom.getBlacklist().add(user);
        chatRoom.getParticipants().remove(user.getClientHandler());
        System.out.println("Пользователь забанен " + user.getLogin());
    }

    public synchronized void unbanUser(User user){
        chatRoom.getBlacklist().remove(user);
        chatRoom.getParticipants().add(user.getClientHandler());
        System.out.println("Пользователь забанен " + user.getLogin());
    }

    public synchronized void muteUser(User user){
        chatRoom.getMutelist().add(new Mute(user, LocalDateTime.now(ZoneId.of("Europe/Moscow")).plusMinutes(1)));
        System.out.println("Пользователь не может говорить");
    }

    public synchronized void unmuteUser(User user){
        for(int i = 0; i<chatRoom.getMutelist().size(); i++){
            if(chatRoom.getMutelist().get(i).getUser().equals(user)){
                chatRoom.getMutelist().remove(i);
                System.out.println("Пользователь может говорить");
                break;
            }
        }
    }

    // геттеры и сеттеры

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
