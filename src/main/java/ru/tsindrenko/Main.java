package ru.tsindrenko;

import com.google.gson.Gson;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

public class Main {

    final static int port = 8080;
    final static String file_directory = "D:\\JavaProjects\\Chat\\src\\main\\resources\\files\\";
    final static DatabaseConnector databaseConnector = new DatabaseConnector();
    static List<ClientHandler> clientHandlerList = new LinkedList<>();
    static List<ChatRoom> chatRooms = new LinkedList<>();
    static List<User> userList = new LinkedList<>();

    public static void main(String [] args) throws IOException{
        chatRooms.addAll(databaseConnector.getChatrooms());
        userList.addAll(databaseConnector.getUsers());
        Gson gson = new Gson();
        Server server = new Server(port, clientHandlerList, chatRooms, userList);
    }

}
