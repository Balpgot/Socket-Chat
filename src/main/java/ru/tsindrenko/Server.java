package ru.tsindrenko;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends Thread {

    private ServerSocket server;
    private List<ClientHandler> clientHandlerList;
    private List<ChatRoom> chatRooms;
    private List<User> userList;

    Server(int port, List<ClientHandler> clientHandlerList, List<ChatRoom> chatRooms, List<User> userList) throws IOException {
       this.server = new ServerSocket(port);
       this.clientHandlerList = clientHandlerList;
       this.chatRooms = chatRooms;
       this.userList = userList;
       start();
       System.out.println("Сервер запущен в потоке " + currentThread().getName());
    }

    @Override
    public void run() {
        Socket socket;
        while (true){
            try {
                System.out.println("Слушаем...");
                socket = server.accept();
                clientHandlerList.add(new ClientHandler(socket, userList));
                clientHandlerList.get(clientHandlerList.size()-1).setChatRoomId(0);
                System.out.println("Клиент подключился");
            }
            catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void stopServer(){
        try {
            server.close();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public Socket getNewClient(Socket socket){
        return socket;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }
}
