package ru.tsindrenko;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private final String type = "CHATROOM";
    private int id;
    private String name;
    private Integer admin_id;
    private transient List<Integer> participants_id;
    private transient List<Integer> blacklist;
    //private transient List<Mute> mutelist;
    private transient Gson gson = new Gson();

    ChatRoom(int id, String name, List<Integer> participants_id){
        this.participants_id = participants_id;
        this.id = id;
        this.name = name;
        this.admin_id = null;
        this.blacklist = new ArrayList<>();
        //this.mutelist = new ArrayList<>();

    }

    ChatRoom(int id, String name){
        this.id = id;
        this.name = name;
        this.admin_id = null;
        this.participants_id = new ArrayList<>();
        this.blacklist = new ArrayList<>();
        //this.mutelist = new ArrayList<>();
    }

    ChatRoom(int id, String name, Integer admin_id){
        this.name = name;
        this.admin_id = admin_id;
        this.participants_id = new ArrayList<>();
        this.blacklist = new ArrayList<>();
        //this.mutelist = new ArrayList<>();
        participants_id.add(admin_id);
    }

    public List<Integer> getParticipants() {
        return participants_id;
    }

    public void sendMessageToAll(TextMessage message){
        for (Integer user_id:participants_id) {
            User user = Main.databaseConnector.getUser(user_id);
            if(user.isOnline()){
                user.getClientHandler().sendMessage(gson.toJson(message,TextMessage.class));
            }

        }
    }

    public void addParticipant(Integer user_id){
        if(!blacklist.contains(user_id)){
            participants_id.add(user_id);
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

    public List<Integer> getParticipants_id() {
        return participants_id;
    }

    public void setParticipants_id(List<Integer> participants_id) {
        this.participants_id = participants_id;
    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Integer> blacklist) {
        this.blacklist = blacklist;
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
