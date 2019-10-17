package ru.tsindrenko;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    private final String type = "USER";
    private int id;
    private String nickname;
    private String login;
    private String password;
    private transient String photo;
    //private transient LocalDateTime muteTime;
    //private transient boolean isMuted;
    private transient boolean isOnline;
    private transient ClientHandler clientHandler;
    private transient List<Integer> chatRooms = new ArrayList<>();

    User(){
        this.nickname = "Anonimus";
        this.login = "login";
        this.password = "password";
    }

    User(int id, String login, String password) {
        this.id = id;
        this.nickname = "Аноним";
        this.login = login;
        this.password = password;
        this.isOnline = false;
    }

    User(String login, String password, String nickname){
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.isOnline = false;
    }

    User(int id, String login, String password, String nickname){
        this.id = id;
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.photo = null;
        this.isOnline = false;
    }

    User(int id, String login, String password, String nickname, String photo){
        this.id = id;
        this.nickname = nickname;
        this.login = login;
        this.password = password;
        this.photo = photo;
        this.isOnline = false;
    }

    //геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public List<Integer> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<Integer> chatRooms) {
        this.chatRooms = chatRooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                login.equals(user.login) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password);
    }
}
