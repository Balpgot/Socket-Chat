package ru.tsindrenko;

import com.google.gson.Gson;

import java.io.IOException;

import java.util.*;

public class Main {

    final static int port = 8080;
    final static String file_directory = "D:\\JavaProjects\\Chat\\src\\main\\resources\\files\\";
    final static DatabaseConnector databaseConnector = new DatabaseConnector();
    static List<ClientHandler> clientHandlerList = new LinkedList<>();
    static HashMap<Integer, ChatRoom> chatRoomMap = new HashMap<>();
    static HashMap<User, ClientHandler> clientHandlerMap = new HashMap<>();
    static HashSet<User> userList = new HashSet<>();

    public static void main(String[] args) {
        List<ChatRoom> chatRoomList = databaseConnector.getChatrooms();
        for (int i = 0; i < chatRoomList.size(); i++) {
            chatRoomMap.put(chatRoomList.get(i).getId(), chatRoomList.get(i));
        }
        System.out.println(chatRoomList);
        userList.addAll(databaseConnector.getUsers());
        Iterator<User> userIterator = userList.iterator();
        while (userIterator.hasNext()){
            clientHandlerMap.put(userIterator.next(), null);
        }
        Gson gson = new Gson();
        Server server = new Server(port, clientHandlerList);
    }
}
