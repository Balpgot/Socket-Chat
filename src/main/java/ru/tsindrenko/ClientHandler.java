package ru.tsindrenko;


import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ClientHandler extends Thread {

    private final String disconnectClient = "BREAK_CONNECTION";
    private final String success = "SUCCESSFUL_OPERATION";
    private final String failure = "FAILED";
    private final String loginInfo = "LOGIN";
    private final String fileInfo = "FILE";
    private final String serviceInfo = "SERVICE";
    private final String chatroomInfo = "CHATROOM";
    private final String userInfo = "USER";
    private final String messageInfo = "TEXT";
    private final String patchInfo = "PATCH_INFO";
    private final String requestInfo = "REQUEST";
    private final String userNotFound = "USER_NOT_FOUND";
    private final String wrongPassword = "WRONG_PASSWORD";
    private final String loginIsOccupied = "LOGIN_IS_OCCUPIED";
    private final String nicknameIsOccupied = "NICKNAME_IS_OCCUPIED";
    private final String userIsLogged = "USER_IS_LOGGED";
    private final String searchChatroomInfo = "SEARCHCHATROOM";
    private final String searchUserInfo = "SEARCHUSER";
    private static final String avatar = "avatar";
    private static final String fileType = "file";
    private final String getRequest = "GET";
    private final String updateRequest = "UPDATE";
    private final String deleteRequest = "DELETE";
    private final String createRequest = "CREATE";

    private Socket clientSocket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private boolean is_active; //активен ли клиент
    private int chatRoomId;//текущий чат
    private User user; //пользователь, закрепленный за данным клиентом
    private Gson gson;


    public ClientHandler(Socket socket) throws IOException{
        this.clientSocket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8));
        this.is_active = true;
        this.user = null;
        this.gson = new Gson();
        Main.clientHandlerList.add(this);
        start(); // вызываем run()
    }

    @Override
    public void run(){
        String message;
        try {
            while (true) {
                //начинаем прослушивать сообщения
                message = in.readLine();
                System.out.println("Сообщение: " + message);
                messageHandler(message);
            }
        }
        catch (SocketException ex){
            System.out.println("SOCKET EX run: " + ex.getMessage());
            ex.printStackTrace();
            endSession();
        }
        catch (IOException ex) {
            System.out.println("CH run: " + ex.getMessage());
            ex.printStackTrace();
            endSession();
        }
    }

    //РАБОТА С ФАЙЛАМИ

    public void sendFile(File file, FileMessage message){
        try {
            //определяем размер пакета и открываем файл на чтение
            byte[] byteArray = new byte[8192];
            FileInputStream fis = new FileInputStream(file.getPath());
            //отправляем клиенту размер файла
            long size = file.length();
            FileMessage fileMessage = new FileMessage(size,fileType,file.getName(),
                    message.getSender_id(), message.getSender_nickname(), message.getChatroom_id(), message.getChatroom_nickname());
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
            File directory = new File(new StringBuffer().
                            append(Main.file_directory).
                            append(user.getId()).
                            append("\\").
                            append(fileMessage.getFileType()).
                            append("\\").toString());
            if(!directory.exists()){
                directory.mkdirs();
            }
            File file = new File(new StringBuffer().
                    append(Main.file_directory).
                    append(user.getId()).
                    append("\\").
                    append(fileMessage.getFileType()).
                    append("\\").
                    append(fileMessage.getFilename()).toString());
            System.out.println(file.getPath());
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
                sendMessageToChatroom(gson.fromJson(message,TextMessage.class));
                break;
            case disconnectClient:
                endSession();
                break;
            case userInfo:
                registration(gson.fromJson(message,User.class));
                break;
            case requestInfo:
                requestHandler(gson.fromJson(message,RequestMessage.class));
                break;
            default: sendMessage("ERROR_MESSAGE");
        }
    }

    public synchronized void sendMessage(String message){
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

    private void requestHandler(RequestMessage message){
        switch (message.getStatus()){
            case getRequest:
                if(message.getClassType().equals(userInfo)){
                    HashSet<User> users = Main.databaseConnector.getUsers();
                    HashMap<String,Integer> searchResult = new HashMap<>();
                    StringBuffer name = new StringBuffer();
                    for (User user:users) {
                        name.append(user.getNickname().toLowerCase());
                        if(name.toString().startsWith(message.getParameter().toLowerCase())){
                            searchResult.put(user.getNickname(),user.getId());
                        }
                        name.setLength(0);
                    }
                    sendMessage(gson.toJson(new ResponseMessage(userInfo, getRequest, success,searchResult)));
                }
                else if(message.getClassType().equals(chatroomInfo)){
                    List<ChatRoom> chatRooms = Main.databaseConnector.getChatrooms();
                    HashMap<String,Integer> searchResult = new HashMap<>();
                    StringBuffer name = new StringBuffer();
                    for (ChatRoom chatRoom:chatRooms) {
                        name.append(chatRoom.getName().toLowerCase());
                        if(name.toString().startsWith(message.getParameter().toLowerCase())){
                            searchResult.put(chatRoom.getName(),chatRoom.getId());
                        }
                        name.setLength(0);
                    }
                    sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, getRequest,success,searchResult)));
                }
                break;
            case updateRequest:
                if(message.getClassType().equals(userInfo)) {
                    HashSet<User> users = Main.databaseConnector.getUsers();
                    boolean status = true;
                    for (User user : users) {
                        if (user.getNickname().equals(message.getUser().getNickname())) {
                            sendMessage(gson.toJson(new ResponseMessage(userInfo, updateRequest, nicknameIsOccupied)));
                            status = false;
                        }
                    }
                    if(status) {
                        if(Main.databaseConnector.updateUser(message.getUser())) {
                            sendMessage(gson.toJson(new ResponseMessage(userInfo, updateRequest, success, message.getUser())));
                        }
                        else {
                            sendMessage(gson.toJson(new ResponseMessage(userInfo, updateRequest, failure)));
                        }
                    }
                }
                else if(message.getClassType().equals(chatroomInfo)){
                    List<ChatRoom> chatRooms = Main.databaseConnector.getChatrooms();
                    boolean status = true;
                    for (ChatRoom chatRoom:chatRooms) {
                        if(chatRoom.getName().equals(message.getChatRoom().getName())){
                            sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, updateRequest, nicknameIsOccupied)));
                            status = false;
                        }
                    }
                    if(status) {
                        if(Main.databaseConnector.updateChatroom(message.getChatRoom())) {
                            sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, updateRequest, success, message.getChatRoom())));
                        }
                        else {
                            sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, updateRequest, failure)));
                        }
                    }
                }
                break;
            case deleteRequest:
                if(message.getClassType().equals(userInfo)){
                    if(Main.databaseConnector.deleteUser(message.getUser().getId())){
                        sendMessage(gson.toJson(new ResponseMessage(userInfo, deleteRequest, success)));
                    }
                    else{
                        sendMessage(gson.toJson(new ResponseMessage(userInfo, deleteRequest, failure)));
                    }
                }
                else if (message.getClassType().equals(chatroomInfo)){
                    if(Main.databaseConnector.deleteChatroom(message.getChatRoom().getId())){
                        sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, deleteRequest, success)));
                    }
                    else{
                        sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, deleteRequest, failure)));
                    }
                }
                break;
            case createRequest:
                if(message.getClassType().equals(chatroomInfo)) {
                    synchronized (Main.databaseConnector){
                        if(Main.databaseConnector.addChatroom(message.getChatRoom())){
                            sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, createRequest, success, Main.databaseConnector.getChatroom(message.getChatRoom().getName()))));
                        }
                        else{
                            sendMessage(gson.toJson(new ResponseMessage(chatroomInfo, createRequest, failure)));
                        }
                    }
                }
                break;
        }
    }

    //отправляет сообщение заданному пользователю
    public synchronized void sendMessageToUser(int userId, TextMessage message){
        for (User user:Main.userList) {
            if(user.getId()==userId){
                Main.clientHandlerMap.get(user).sendMessage(gson.toJson(message));
            }
        }
    }

    //отправляет сообщение в заданную чат-комнату
    private void sendMessageToChatroom(TextMessage message){
        if(message.getChatroom_id()==0){
            Main.chatRoomMap.get(1).sendMessageToAll(message);
        }
        else{
            Main.chatRoomMap.get(message.getChatroom_id()).sendMessageToAll(message);
        }
    }

    //метод входа/регистрации в чате
    private boolean login(LoginMessage loginMessage) {
        boolean user_found;
        String login = loginMessage.getLogin(), password = loginMessage.getPassword();
        user_found = false;
        //Ищем пользователя с введенным логином
        HashSet<User> userList = Main.userList;
        for (User user : userList)
            if (user.getLogin().equals(login)) {
                user_found = true;
                System.out.println("Пользователь найден: " + user.getLogin());
                this.user = user;
            }
        //если пользователь не найден сообщаем клиенту
        if (!user_found) {
            sendMessage(gson.toJson(new ServiceMessage(loginInfo, userNotFound)));
            return false;
        }
        //если уже авторизован сообщаем клиенту
        if (user.isOnline()) {
            sendMessage(gson.toJson(new ServiceMessage(loginInfo, userIsLogged)));
            return false;
        }
        //проверяем пароль
        synchronized (user) {
            if (user.getPassword().equals(password)) {
                //меняем статус пользователя
                user.setOnline(true);
                this.chatRoomId = 1;
                user.setClientHandler(this);
                //сообщаем клиенту об успешном входе, отправляем сервисные сообщения
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, success)));
                sendMessage(gson.toJson(user));
                sendMessage(gson.toJson(user.getChatRooms()));
                //приветствуем
                sendMessage(gson.toJson(new TextMessage("Добро пожаловать в чат",0, "SERVER", 1, "Общий чат")));
                //сообщаем БД о статусе пользователя
                Main.databaseConnector.makeUserOnline(user.getId(),true);
                //добавляем соответствие CH и пользователя
                Main.clientHandlerMap.put(user,this);
                //отправляем все отложенные сообщения
                while (user.getMessageQueue().size()>0){
                    sendMessage(user.getMessageQueue().poll());
                }
                return true;
            } else {
                //отправляем сообщение о неверном пароле
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, wrongPassword)));
                this.user = null;
                return false;
            }
        }
    }

    private boolean registration(User user) {
        //Ищем пользователей с полученным логином
        System.out.println("Поиск по пользователям");
        HashSet<User> userList = Main.userList;
        for (User currentUser : userList) {
            if (currentUser.getLogin().equals(user.getLogin())) {
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, loginIsOccupied)));
                return false;
            }
            if (currentUser.getNickname().equals(user.getNickname())) {
                sendMessage(gson.toJson(new ServiceMessage(loginInfo, loginIsOccupied)));
                return false;
            }
        }

        //Добавляем запись о новом пользователе
        this.user = new User(user.getLogin(), user.getPassword(), user.getNickname());
        this.user.setOnline(true);
        //добавляем пользователя в общий список
        Main.userList.add(user);
        //добавляем пользователя в общий чат
        user.getChatRooms().add(1);
        this.chatRoomId = 1;
        //добавляем пользователя в БД
        Main.databaseConnector.addUser(user);
        //устанавливаем ID пользователя
        this.user.setId(Main.databaseConnector.getUser(user.getNickname()).getId());
        System.out.println(user.getLogin() + " " + user.getPassword());
        //отсылаем на клиент серверные сообщения
        sendMessage(gson.toJson(new ServiceMessage(loginInfo, success)));
        sendMessage(gson.toJson(this.user));
        sendMessage(gson.toJson(user.getChatRooms()));
        //отсылаем приветствие
        sendMessage(gson.toJson(new TextMessage("Добро пожаловать в чат",0,"SERVER", 1, "Общий чат")));
        //обозначаем CH для польователя
        user.setClientHandler(this);
        Main.clientHandlerMap.put(user,this);
        return true;
    }

    //завершает текущую сессию связи
    public synchronized void endSession(){
        if(clientSocket.isConnected()){
            sendMessage(disconnectClient);
        }
        System.out.println("Клиент отключился");
        is_active = false;
        //закрываем потоки, убираем из списков
        try {
            in.close();
            out.close();
            Main.clientHandlerList.remove(this);
            Main.clientHandlerMap.remove(user);
        }
        catch (IOException ex) {
            System.out.println("endsession: " + ex.getMessage());
        }
        //удаляем данные о пользователе
        if(user!=null){
            user.setOnline(false);
            Main.databaseConnector.makeUserOnline(user.getId(),false);
            user.setClientHandler(null);
        }
    }

    // ГЕТТЕРЫ И СЕТТЕРЫ

    public void setChatRoomId(int chatRoomId){
        this.chatRoomId = chatRoomId;
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

    public int getChatRoomId() {
        return chatRoomId;
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

    @Override
    public String toString() {
        return "ClientHandler{" +
                "is_active=" + is_active +
                ", user=" + user +
                '}';
    }
}
