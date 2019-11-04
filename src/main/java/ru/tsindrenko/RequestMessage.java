package ru.tsindrenko;

public class RequestMessage {
    private final String type = "REQUEST";
    private String classType;
    private String status;
    private User user;
    private ChatRoom chatRoom;
    private String parameter;

    public RequestMessage(String classType, String status, String parameter) {
        this.classType = classType;
        this.status = status;
        this.parameter = parameter;
    }

    public RequestMessage(String classType, String status, User user) {
        this.classType = classType;
        this.status = status;
        this.user = user;
    }

    public RequestMessage(String classType, String status, ChatRoom chatRoom) {
        this.classType = classType;
        this.status = status;
        this.chatRoom = chatRoom;
    }

    public RequestMessage(String classType, String status, User user, ChatRoom chatRoom, String parameter) {
        this.classType = classType;
        this.status = status;
        this.user = user;
        this.chatRoom = chatRoom;
        this.parameter = parameter;
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

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
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