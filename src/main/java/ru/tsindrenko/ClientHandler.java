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
    private ChatRoom chatRoom;//текущий чат
    private List<User> userList;
    private User user;


    public ClientHandler(Socket socket, List<User> userList) throws IOException{
        this.clientSocket = socket;
        this.userList = userList;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        is_active = true;
        this.user = null;
        start(); // вызываем run()
    }

    @Override
    public void run(){
        String message;
        boolean isLogged = false;
        try {
            while (true) {
                while (!isLogged){
                    isLogged = login();
                }
                sendMessage("Добро пожаловать в " + chatRoom.getName());
                message = in.readLine();
                System.out.println("Сообщение в потоке " + currentThread().getName());
                if(message.equals("stop")) {
                    sendMessage(disconnectClient);
                    System.out.println("Клиент отключился");
                    is_active = false;
                    user.setLogged(false);
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
        boolean user_found;
        System.out.println(userList);
        while (true){
            user_found = false;
            sendMessage("Вы зарегистрированы? да/нет");
            message = in.readLine();
            switch (message) {
                case "да":
                    sendMessage("Введите логин или введите exit");
                    message = in.readLine();
                    if(message.equals("exit")){
                        break;
                    }
                    //Ищем пользователя с введенным логином
                    for (User user : userList)
                        if (user.getLogin().equals(message)){
                            user_found = true;
                            System.out.println("Пользователь найден: " + user.getLogin());
                            this.user = user;
                        }
                    //Если нет - начинаем вхождение сначала
                    if(!user_found){
                        System.out.println("Пользователя с таким логином не найдено.");
                        break;
                    }
                    //проверяем пароль
                    synchronized (user) {
                        sendMessage("Введите пароль или введите exit");
                        message = in.readLine();
                        if(message.equals("exit")){
                            this.user = null;
                            break;
                        }
                        if (user.getPassword().equals(message)) {
                            sendMessage("Здравствуйте, " + user.getNickname());
                            user.setLogged(true);
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
                            return false;
                        }
                        if (!message.isEmpty()) {
                            //Ищем пользователей с полученным логином
                            System.out.println("Поиск по пользователям");
                            for (User user : userList)
                                if (user.getLogin().equals(message)) {
                                    sendMessage("Логин занят");
                                    user_found = true;
                                }
                            //Записываем новый логин если не нашли пользователя с таким логином
                            if(!user_found){
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
                    //Добавляем запись о новом пользователе
                    this.user = new User(userList.size() - 1, login, password);
                    if(user.getId()<0){
                        user.setId(0);
                    }
                    userList.add(user);
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
