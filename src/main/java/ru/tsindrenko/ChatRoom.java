package ru.tsindrenko;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int id;
    private String name;
    private List<ClientHandler> participants;
    private List<User> blacklist;
    private List<Mute> mutelist;

    ChatRoom(int id, String name, List<ClientHandler> clientHandlerList){
        this.participants = clientHandlerList;
        this.id = id;
        this.name = name;
        this.blacklist = new ArrayList<>();
        this.mutelist = new ArrayList<>();
    }

    public List<ClientHandler> getParticipants() {
        return participants;
    }

    public void sendMessageToAll(String message){
        for (ClientHandler client:participants) {
            client.sendMessage("Чат " + id + " " + message);
        }
    }

    public void addParticipant(ClientHandler handler){
        if(!blacklist.contains(handler.getUser())){
            participants.add(handler);
        }
    }

    //геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParticipants(List<ClientHandler> participants) {
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<User> blacklist) {
        this.blacklist = blacklist;
    }

    public List<Mute> getMutelist() {
        return mutelist;
    }

    public void setMutelist(List<Mute> mutelist) {
        this.mutelist = mutelist;
    }
}
