package ru.tsindrenko;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private final static int port = 8080;
    private static List<ClientHandler> clientHandlerList = new LinkedList<>();
    private static List<ChatRoom> chatRooms = new LinkedList<>();
    private static List<User> userList = new LinkedList<>();

    public static void main(String [] args) throws IOException{
        chatRooms.add(new ChatRoom(0,clientHandlerList));
        Server server = new Server(port, clientHandlerList, chatRooms, userList);
        ClientListManager clientListManager = new ClientListManager(clientHandlerList);
    }

}
