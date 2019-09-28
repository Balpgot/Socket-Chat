package ru.tsindrenko;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String nickname;
    private String login;
    private String password;
    private String photo;
    private LocalDateTime muteTime;
    private boolean isMuted;
    private boolean isLogged;

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
        this.isMuted = false;
        this.isLogged = false;
    }

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

    public LocalDateTime getMuteTime() {
        return muteTime;
    }

    public void setMuteTime(LocalDateTime muteTime) {
        this.muteTime = muteTime;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
