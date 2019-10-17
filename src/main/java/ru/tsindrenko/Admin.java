package ru.tsindrenko;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Admin extends User {

    private static Integer chatRoom_id;

    Admin(){
    }

    Admin(Integer chatRoom_id){
        this.chatRoom_id = chatRoom_id;
    }

    public static synchronized void banUser(Integer user_id){
        Main.databaseConnector.gchatRoom_id.getBlacklist().add(user_id);
        chatRoom_id.getParticipants().remove(user_id);
    }

    public static synchronized void unbanUser(Integer user_id){
        chatRoom_id.getBlacklist().remove(user_id);
        chatRoom_id.getParticipants().add(user_id);
    }

    /*public synchronized void muteUser(User user){
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
*/
    // геттеры и сеттеры

    public Integer getChatRoom_id() {
        return chatRoom_id;
    }

    public void setChatRoom_id(Integer chatRoom_id) {
        this.chatRoom_id = chatRoom_id;
    }
}
