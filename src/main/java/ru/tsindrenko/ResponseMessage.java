package ru.tsindrenko;

import java.util.HashMap;

public class ResponseMessage {
    private final String type = "RESPONSE";
    private String classType;
    private String action;
    private String status;
    private HashMap<String,Integer> body;
    private User user;
    private ChatRoom chatRoom;
    private String parameter;

    public ResponseMessage(String classType, String action, String status, HashMap<String,Integer> body) {
        this.classType = classType;
        this.action = action;
        this.status = status;
        this.body = body;
    }

    public ResponseMessage(String classType, String action, String status, User user) {
        this.classType = classType;
        this.action = action;
        this.status = status;
        this.user = user;
    }

    public ResponseMessage(String classType, String action, String status, ChatRoom chatRoom) {
        this.classType = classType;
        this.action = action;
        this.status = status;
        this.chatRoom = chatRoom;
    }

    public ResponseMessage(String classType, String action, String status, HashMap<String, Integer> body, String parameter) {
        this.classType = classType;
        this.action = action;
        this.status = status;
        this.body = body;
        this.parameter = parameter;
    }

    public ResponseMessage(String classType, String action, String status, HashMap<String, Integer> body, User user, String parameter) {
        this.classType = classType;
        this.action = action;
        this.status = status;
        this.body = body;
        this.user = user;
        this.parameter = parameter;
    }

    public ResponseMessage(String classType, String action, String status) {
        this.classType = classType;
        this.action = action;
        this.status = status;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HashMap<String,Integer> getBody() {
        return body;
    }

    public void setBody(HashMap<String,Integer> body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
