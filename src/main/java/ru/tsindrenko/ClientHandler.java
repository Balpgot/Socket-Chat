package ru.tsindrenko;


import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private final String disconnectClient = "BREAK_CONNECTION";
    private final String getFile = "FILE";
    private final String accepted = "SUCCESSFUL_OPERATION";
    private Socket clientSocket; // сокет, через который сервер общается с клиентом,
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private boolean is_active; //активен ли клиент
    private ChatRoom chatRoom;//текущий чат
    private List<User> userList; // список всех пользователей
    private User user; //пользователь, закрепленный за данным клиентом


    public ClientHandler(Socket socket, List<User> userList) throws IOException{
        this.clientSocket = socket;
        this.userList = userList;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.is_active = true;
        this.user = null;
        start(); // вызываем run()
    }

    @Override
    public void run(){
        String message;
        boolean isLogged = false;
        try {
            while (true) {
                //логинимся в чат
                while (!isLogged){
                    isLogged = login();
                }
                //начинаем прослушивать сообщения
                message = in.readLine();
                System.out.println("Сообщение: " + message);
                //если отправлен файл
                if(message.equals(getFile)){
                    System.out.println("Инициирована передача файла");
                    message = in.readLine();
                    receiveFile(message);
                    System.out.println("Файл получен");
                }
                else {
                    //чистим мат
                    for (String word : Main.swearWords) {
                        if (message.contains(word)) {
                            message = message.replaceAll(word, "*** ");
                        }
                    }
                    System.out.println("Сообщение в потоке " + currentThread().getName());
                    //получаем сообщение об остановке клиента
                    if (message.equals("stop")) {
                        endSession();
                    } else
                        chatRoom.sendMessageToAll(message);
                    //sendMessage(message);
                }
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
        /** TODO
         * Разработать метод
         */
    }

    public void receiveFile(String filename){
        try {
            String message = in.readLine();
            //получаем размер файла
            long size = Long.parseLong(message);
            System.out.println("Размер файла: " + size);
            //объявляем размер пакета
            byte [] bytes = new byte[8192];
            //устанавливаем файл на запись
            File file = new File(Main.file_directory +"//"+ filename);
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
            System.out.println("receive file: " + ex.getMessage());
        }
    }

    //ПРОСТЫЕ СООБЩЕНИЯ

    public void sendMessage(String message){
        try {
            out.write(message+"\n");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println("sendMessage: " + ex.getMessage());
            System.out.println("Сообщение с исключением: " + message);
        }
    }

    //отправляет сообщение заданному пользователю
    public void sendMessageToUser(User user, String message){
        if(user.isLogged()){
            user.getClientHandler().sendMessage(message);
        }
    }

    //отправляет сообщение в заданную чат-комнату
    private void sendMessage(String message, int chatroom_id){
        try {
            out.write(message+"\n");
            out.flush();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //метод входа/регистрации в чате
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
                    System.out.println(login + " " + password);
                    sendMessage("Вы успешно зарегистрированы \n"
                            + "Ваш логин: " + login + "\n"
                            + "Ваш пароль: " + password);
                    sendMessage("Добро пожаловать в " + chatRoom.getName());
                    return true;
            }
        }
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
