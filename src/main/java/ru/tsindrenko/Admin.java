package ru.tsindrenko;

public class Admin extends User {

    private ChatRoom chatRoom;

    Admin(){
    }

    Admin(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    public void banUser(User user){
        chatRoom.getBlacklist().add(user);
        chatRoom.getParticipants().remove(user);
    }

    // геттеры и сеттеры

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
