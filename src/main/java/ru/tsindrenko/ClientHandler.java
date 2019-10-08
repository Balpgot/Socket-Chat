package ru.tsindrenko;


import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class ClientHandler extends Thread {

    private final String disconnectClient = "BREAK_CONNECTION";
    private final String accepted = "SUCCESSFUL_OPERATION";
    private final String loginInfo = "LOGIN";
    private final String fileInfo = "FILE";
    private final String serviceInfo = "SERVICE";
    private final String messageInfo = "TEXT";
    private final String patchInfo = "PATCH_INFO";
    private final String requestInfo = "REQUEST_INFO";
    private final String userNotFound = "USER_NOT_FOUND";
    private final String wrongPassword = "WRONG_PASSWORD";
    private final String loginIsOccupied = "LOGIN_IS_OCCUPIED";
    private final String userIsLogged = "USER_IS_LOGGED";
    private static final String avatar = "avatar";
    private static final String fileType = "file";
    private Socket clientSocket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private boolean is_active; //активен ли клиент
    private ChatRoom chatRoom;//текущий чат
    private int dialogUserId;//id пользователя - собеседника
    private List<User> userList; // список всех пользователей
    private User user; //пользователь, закрепленный за данным клиентом
    private Gson gson;


    public ClientHandler(Socket socket, List<User> userList) throws IOException{
        this.clientSocket = socket;
        this.userList = userList;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.is_active = true;
        this.user = null;
        this.gson = new Gson();
        start(); // вызываем run()
    }

    @Override
    public void run(){
        String message;
        try {
            while (true) {
                //начинаем прослушивать сообщения
                message = in.readLine();
                messageHandler(message);
                System.out.println("Сообщение: " + message);
            }
        }
        catch (IOException ex) {
            System.out.println("CH run: " + ex.getMessage());
            ex.printStackTrace();
            endSession();
        }
    }

    //РАБОТА С ФАЙЛАМИ

    public void sendFile(File file){
        try {
            //определяем размер пакета и открываем файл на чтение
            byte[] byteArray = new byte[8192];
            FileInputStream fis = new FileInputStream(file.getPath());
            //отправляем клиенту размер файла
            long size = file.length();
            FileMessage fileMessage = new FileMessage(size,fileType);
            sendMessage(gson.toJson(fileMessage));
            System.out.println("Начинаю оправлять");
            BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
            while (size>0){
                int i = fis.read(byteArray);
                bos.write(byteArray, 0, i);
                size-= i;
            }
            bos.flush();
            fis.close();
        }
        catch (IOException ex){
            System.out.println("sendFile: " + ex.getMessage());
        }
    }

    public void receiveFile(FileMessage fileMessage){
        try {
            //получаем размер файла
            long size = fileMessage.getSize();
            System.out.println("Размер файла: " + size);
            //объявляем размер пакета
            byte [] bytes = new byte[8192];
            //устанавливаем файл на запись
            File file = new File(new StringBuffer().
                            append(Main.file_directory).
                            append(user.getId()).
                            append("//").
                            append(fileMessage.getFileType()).
                            append("//").
                            append("1.txt").toString());
            file.createNewFile();
            if(file.exists()){
                System.out.println("Файл существует");
            }
            else
                System.out.println("Файл не существует");
            //запускаем поток записи в файл
            FileOutputStream fileWriter = new FileOutputStream(file);
            //объявляем поток откуда пойдут данные
            BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
            int i;
            //считываем данные пока они не закончатся
            while (size>0){
              i = bis.read(bytes);
              fileWriter.write(bytes,0,i);
              size-=i;
            }
            fileWriter.close();
        }
        catch (IOException ex) {
            //System.out.println("receive file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    //СООБЩЕНИЯ
    private void messageHandler(String message){
        JSONObject json = new JSONObject(message);
        String header = json.get("type").toString();
        switch (header){
            case loginInfo:
                login(gson.fromJson(message,LoginMessage.class));
                break;
            case fileInfo:
                receiveFile(gson.fromJson(message,FileMessage.class));
                break;
            case messageInfo:
                messageRouter(gson.fromJson(message,TextMessage.class));
                break;
            case disconnectClient:
                endSession();
                break;
            default: sendMessage("ERROR_MESSAGE");
        }
    }

    private void messageRouter(TextMessage message){
        message.setSender_id(user.getId());
        /*if(message.getUser_id()>=0){
            sendMessageToUser(message.getUser_id(),message);
        }
        else
            sendMessageToChatroom(message);*/
        sendMessage(gson.toJson(message));
    }

    public void sendMessage(String message){
        try {
            out.write(message+"\n");
            out.flush();
            System.out.println("Сообщение отправлено " + message);
        }
        catch (IOException ex) {
            System.out.println("sendMessage: " + ex.getMessage());
            System.out.println("Сообщение с исключением: " + message);
        }
    }

    //отправляет сообщение заданному пользователю
    public synchronized void sendMessageToUser(int userId, TextMessage message){
        for (User user:Main.userList) {
            if(user.getId()==userId){
                user.getClientHandler().sendMessage(gson.toJson(message));
            }
        }
    }

    //отправляет сообщение в заданную чат-комнату
    private void sendMessageToChatroom(TextMessage message){
        chatRoom.sendMessageToAll(message);
    }

    //метод входа/регистрации в чате
    private boolean login(LoginMessage loginMessage) {
        boolean user_found;
        String login = loginMessage.getLogin(), password = loginMessage.getPassword();
        user_found = false;
        //Ищем пользователя с введенным логином
        for (User user : userList)
            if (user.getLogin().equals(login)) {
                user_found = true;
                System.out.println("Пользователь найден: " + user.getLogin());
                this.user = user;
            }
        if (!user_found) {
            sendMessage(gson.toJson(new ServiceMessage(loginInfo, userNotFound)));
            return false;
        }

        if (user.isLogged()) {
            sendMessage(gson.toJson(new ServiceMessage(loginInfo, userIsLogged)));
            return false;
        }
        //проверяем пароль
        synchronized (user) {
            if (user.getPassword().equals(password)) {
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, accepted)));
                user.setLogged(true);
                sendMessage("USER"+gson.toJson(user));
                sendMessage(gson.toJson(new TextMessage("Добро пожаловать в чат",0, -1, user.getId(),"SERVER")));
                return true;
            } else {
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, wrongPassword)));
                this.user = null;
                return false;
            }
        }
        /* TODO
            сделать метод для зарегистрированных пользователей
         */
                /*//Ищем пользователей с полученным логином
                System.out.println("Поиск по пользователям");
                for (User user : userList)
                    if (user.getLogin().equals(login)) {
                        sendMessage(loginIsOccupied);
                        break;
                    }

                //Добавляем запись о новом пользователе
                this.user = new User(userList.size() - 1, login, password);
                if (user.getId() < 0) {
                    user.setId(0);
                }
                this.user.setLogged(true);
                userList.add(user);
                System.out.println(login + " " + password);
                sendMessage(accepted);
                return true;*/
    }

    //завершает текущую сессию связи
    public synchronized void endSession(){
        if(clientSocket.isConnected()){
            sendMessage(disconnectClient);
        }
        System.out.println("Клиент отключился");
        is_active = false;
        if(!(user==null)){
            Main.userList.remove(user);
            user.setLogged(false);
            user.setClientHandler(null);
        }
        try {
            in.close();
            out.close();
            Main.clientHandlerList.remove(this);
        }
        catch (IOException ex) {
            System.out.println("endsession: " + ex.getMessage());
        }
    }

    // ГЕТТЕРЫ И СЕТТЕРЫ

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

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
