package ru.tsindrenko;

import java.util.List;

public class ChatRoom {
    private int id;
    private String name;
    private List<ClientHandler> participants;

    ChatRoom(int id, String name, List<ClientHandler> clientHandlerList){
        this.participants = clientHandlerList;
        this.id = id;
        this.name = name;
    }

    public void addParticipant(ClientHandler participant){
        participants.add(participant);
    }

    public List<ClientHandler> getParticipants() {
        return participants;
    }

    public void sendMessageToAll(String message){
        for (ClientHandler client:participants) {
            client.sendMessage("Чат " + id + " " + message);
        }
    }

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
}
