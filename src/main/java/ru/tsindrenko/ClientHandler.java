package ru.tsindrenko;


import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private final String disconnectClient = "break_connection";
    private Socket clientSocket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private boolean is_active;
    private boolean is_logged;
    private ChatRoom chatRoom;//текущий чат
    private List<User> userList;
    private User user;


    public ClientHandler(Socket socket, List<User> userList) throws IOException{
        this.clientSocket = socket;
        this.userList = userList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        is_active = true;
        this.is_logged = false;
        this.user = null;
        start(); // вызываем run()
    }

    @Override
    public void run(){
        String message;
        try {
            while (true) {
                login();
                message = in.readLine();
                System.out.println("Сообщение в потоке " + currentThread().getName());
                if(message.equals("stop")) {
                    sendMessage(disconnectClient);
                    System.out.println("Клиент отключился");
                    is_active = false;
                    break;
                }
                else
                    chatRoom.sendMessageToAll(message);
                    //sendMessage(message);
            }
            in.close();
            out.close();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void sendMessage(String message){
        try {
            out.write(message+"\n");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void sendMessage(String message, int chatroom_id){
        try {
            out.write(message+"\n");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean login() throws IOException{
        String message, login = "", password = "";
        boolean login_accepted = false;
        boolean password_accepted = false;
        while (true){
            sendMessage("Вы зарегистрированы?");
            message = in.readLine();
            switch (message) {
                case "да":
                    sendMessage("Введите логин");
                    message = in.readLine();
                    for (User user : userList)
                        if (user.getLogin().equals(message))
                            this.user = user;
                    synchronized (user) {
                        sendMessage("Введите пароль");
                        message = in.readLine();
                        if (user.getPassword().equals(message)) {
                            sendMessage("Здравствуйте, " + user.getNickname());
                            return true;
                        } else
                            sendMessage("Неверный логин или пароль");
                    }
                    break;
                case "нет":
                    while (!login_accepted) {
                        sendMessage("Введите логин или отправьте exit");
                        message = in.readLine();
                        if (message.equals("exit")) {
                            break;
                        }
                        if (!message.isEmpty()) {
                            for (User user : userList)
                                if (user.getLogin().equals(message))
                                    sendMessage("Логин занят");
                                else {
                                    login = message;
                                    login_accepted = true;
                                }
                        } else
                            sendMessage("Логин не может быть пустым");
                    }

                    while (!password_accepted) {
                        sendMessage("Введите пароль или отправьте exit");
                        message = in.readLine();
                        if (message.equals("exit")) {
                            break;
                        }
                        if (!message.isEmpty()) {
                            password = message;
                            password_accepted = true;
                        } else
                            sendMessage("Пароль не может быть пустым");
                    }
                    userList.add(new User(userList.size() - 1, login, password));
                    sendMessage("Вы успешно зарегистрированы \n"
                            + "Ваш логин: " + login + "\n"
                            + "Ваш пароль: " + password);
                    return true;
            }
        }

    }

    // геттеры и сеттеры

    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    public boolean isActive(){
        return is_active;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void setActive(boolean active) {
        this.is_active = active;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
