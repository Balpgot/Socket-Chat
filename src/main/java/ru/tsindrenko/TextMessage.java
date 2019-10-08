package ru.tsindrenko;

public class TextMessage {
    private final String type = "TEXT";
    private String body;
    private int sender_id;
    private int chatroom_id;
    private int user_id;
    private String sender_nickname;

    public TextMessage(String body, int sender_id, int chatroom_id, int user_id, String sender_nickname) {
        this.body = body;
        this.sender_id = sender_id;
        this.chatroom_id = chatroom_id;
        this.user_id = user_id;
        this.sender_nickname = sender_nickname;
    }

    public String getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSender_nickname() {
        return sender_nickname;
    }

    public void setSender_nickname(String sender_nickname) {
        this.sender_nickname = sender_nickname;
    }
}
