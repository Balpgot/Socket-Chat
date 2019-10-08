package ru.tsindrenko;

public class LoginMessage {
    private final String type = "LOGIN";
    private String login;
    private String password;

    public LoginMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getType() {
        return type;
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
}
