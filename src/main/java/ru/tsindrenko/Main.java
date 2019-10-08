package ru.tsindrenko;

import com.google.gson.Gson;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

public class Main {

    final static int port = 8080;
    final static String file_directory = "D:\\JavaProjects\\Chat\\src\\main\\resources\\files\\";
    static List<ClientHandler> clientHandlerList = new LinkedList<>();
    static List<ChatRoom> chatRooms = new LinkedList<>();
    static List<User> userList = new LinkedList<>();

    public static void main(String [] args) throws IOException{
        chatRooms.add(new ChatRoom(0,"Общий чат",clientHandlerList));
        userList.add(new User(1,"bal","123"));
        Gson gson = new Gson();
        System.out.println(gson.toJson(userList.get(0)));
        Server server = new Server(port, clientHandlerList, chatRooms, userList);
    }

}
