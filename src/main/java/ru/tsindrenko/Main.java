package ru.tsindrenko;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    final static int port = 8080;
    final static String file_directory = "C://DOS//";
    static List<ClientHandler> clientHandlerList = new LinkedList<>();
    static List<ChatRoom> chatRooms = new LinkedList<>();
    static List<User> userList = new LinkedList<>();
    static List<String> swearWords = new ArrayList<>();

    public static void main(String [] args) throws IOException{
        chatRooms.add(new ChatRoom(0,"Общий чат",clientHandlerList));
        Server server = new Server(port, clientHandlerList, chatRooms, userList);
    }

}
