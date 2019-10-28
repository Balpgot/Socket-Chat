package ru.tsindrenko;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ChatRoom {
    private final String type = "CHATROOM";
    private int id;
    private String name;
    private Integer admin_id;
    private boolean is_dialog;
    private transient HashSet<Integer> participants_id;
    private transient List<Integer> blacklist;
    private transient Gson gson = new Gson();

    ChatRoom(int id, String name, HashSet<Integer> participants_id){
        this.participants_id = participants_id;
        this.id = id;
        this.name = name;
        this.admin_id = null;
        this.blacklist = new ArrayList<>();
    }

    ChatRoom(int id, String name){
        this.id = id;
        this.name = name;
        this.admin_id = null;
        this.participants_id = new HashSet<>();
        this.blacklist = new ArrayList<>();
    }

    ChatRoom(int id, String name, Integer admin_id){
        this.id = id;
        this.name = name;
        this.admin_id = admin_id;
        this.participants_id = new HashSet<>();
        this.blacklist = new ArrayList<>();
        participants_id.add(admin_id);
    }

    ChatRoom(String name, Integer admin_id){
        this.name = name;
        this.admin_id = admin_id;
        this.participants_id = new HashSet<>();
        this.blacklist = new ArrayList<>();
        participants_id.add(admin_id);
    }

    public HashSet<Integer> getParticipants() {
        return participants_id;
    }

    public void sendMessageToAll(TextMessage message){
        System.out.println(participants_id);
        User user;
        ClientHandler clientHandler;
        //объявляем итератор по списку пользователей
        Iterator<User> userIterator=Main.userList.iterator();
        /*проходим по каждому пользователю
        * если он есть в списке участников, запрашиваем его CH
        * отправляем сообщение
        */
        while(userIterator.hasNext()){
            user = userIterator.next();
            if(participants_id.contains(user.getId())){
                System.out.println("USER: "+user );
                clientHandler = Main.clientHandlerMap.get(user);
                if(user.isOnline()){
                    clientHandler.sendMessage(gson.toJson(message,TextMessage.class));
                }
                else
                    user.getMessageQueue().add(gson.toJson(message));
            }
        }
    }

    public void addParticipant(Integer user_id){
        if(is_dialog && participants_id.size()<2) {
            if (!blacklist.contains(user_id)) {
                participants_id.add(user_id);
                Main.databaseConnector.addUserToChatroom(this.id, user_id, false);
            }
        }
    }

    //геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Integer admin_id) {
        this.admin_id = admin_id;
    }

    public HashSet<Integer> getParticipants_id() {
        return participants_id;
    }

    public void setParticipants_id(HashSet<Integer> participants_id) {
        this.participants_id = participants_id;
    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Integer> blacklist) {
        this.blacklist = blacklist;
    }

    public boolean isDialog() {
        return is_dialog;
    }

    public void setIsDialog(boolean is_dialog) {
        this.is_dialog = is_dialog;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", admin=" + admin_id +
                '}';
    }
}
