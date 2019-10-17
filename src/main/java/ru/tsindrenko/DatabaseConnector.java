package ru.tsindrenko;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

    private static final String url = "jdbc:mysql://localhost:3306/serverDB?serverTimezone=Europe/Moscow&useSSL=false";
    private static final String login = "root";
    private static final String password = "root";

    private static Connection connection;

    DatabaseConnector(){
        try {
            // opening database connection to MySQL server
            connection = DriverManager.getConnection(url, login, password);
            // getting Statement object to execute query

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    //методы для работы

    private ResultSet executeQuery(String query){
        ResultSet resultSet = null;
        Statement statement;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return resultSet;
    }

    public void endConnection(){
            //close connection ,stmt
        try { connection.close(); } catch(SQLException se) { /*can't do anything */ }
    }


    //методы для получения данных

    public List<User> getUsers(){
        ResultSet users = executeQuery("SELECT * FROM users WHERE is_deleted=0");
        List<User> userList = new ArrayList<>();
        User user;
        try{
            while (users.next()) {
                user = new User(
                        users.getInt(1),
                        users.getString(2),
                        users.getString(3),
                        users.getString(4),
                        users.getString(5));
                user.setChatRooms(getUserChatrooms(user.getId()));
                userList.add(user);
            }
            users.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<ChatRoom> getChatrooms(){
        ResultSet resultSet = executeQuery("SELECT * FROM chatrooms WHERE is_deleted=0");
        List<ChatRoom> chatRoomList = new ArrayList<>();
        ChatRoom chatRoom;
        try{
            while (resultSet.next()) {
                chatRoom = new ChatRoom(
                        resultSet.getInt(1),
                        resultSet.getString(2));
                chatRoom.getParticipants().addAll(getChatroomParticipants(chatRoom.getId()));
                chatRoom.setAdmin_id(getUser(resultSet.getInt(3)).getId());
                chatRoomList.add(chatRoom);
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return chatRoomList;
    }

    public User getUser(int id){
        ResultSet userDB = executeQuery("SELECT * FROM users WHERE id="+id);
        User user = null;
        try {
             userDB.next();
             user = new User(userDB.getInt(1),
                    userDB.getString(2),
                    userDB.getString(3),
                    userDB.getString(4),
                    userDB.getString(5));
             userDB.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return user;
    }

    public ChatRoom getChatroom(int id){
        ResultSet chatroomDB = executeQuery("SELECT * FROM chatrooms WHERE id="+id);
        ChatRoom chatRoom = null;
        try {
            chatroomDB.next();
            chatRoom = new ChatRoom(chatroomDB.getInt(1),
                    chatroomDB.getString(2),
                    chatroomDB.getInt(3));
            chatroomDB.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return chatRoom;
    }

    public List<Integer> getUserChatroomsId(int id){
        ResultSet resultSet = executeQuery("SELECT chatrooms.id,chatrooms.name,chatrooms.administrator " +
                "FROM chatrooms,chatroom_users " +
                "WHERE chatrooms.is_deleted=0 AND chatroom_users.user_id =" + id + " AND chatroom_users.chatroom_id = chatrooms.id");
        List<ChatRoom> chatRoomList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                chatRoomList.add(new ChatRoom(
                        resultSet.getInt(1),
                        resultSet.getString(2)));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return chatRoomList;
    }

    public List<Integer> getChatroomParticipantsId(int id){
        ResultSet resultSet = executeQuery("SELECT users.id" +
                "FROM users,chatroom_users " +
                "WHERE users.is_deleted=0 AND chatroom_users.chatroom_id =" + id + " AND chatroom_users.user_id = users.id");
        List<Integer> userList = new ArrayList<>();
        try{
            while (resultSet.next()) {
               userList.add(resultSet.getInt(1));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public void addUser(User user){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (users.login,users.password,users.nickname,users.avatar,users.is_deleted) VALUES ('" +
                    user.getLogin() + "','" + user.getPassword() + "','" +
                    user.getNickname() + "','" + user.getPhoto()+ "','0')");
            user.setId(executeQuery("SELECT id FROM users WHERE login="+user.getLogin()).getInt(1));
            System.out.println(getUsers());
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void addChatroom(ChatRoom chatRoom){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO chatrooms (chatrooms.name,chatrooms.administrator,chatrooms.is_deleted) VALUES ('" +
                    chatRoom.getName() + "','" + chatRoom.getAdmin_id() + "'," +
                      "'0')");
            ResultSet resultSet = executeQuery("SELECT id FROM chatrooms WHERE name='"+chatRoom.getName()+"'");
            resultSet.next();
            chatRoom.setId(resultSet.getInt(1));
            statement.close();
            resultSet.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }


    //геттеры сеттеры
    public  String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        DatabaseConnector.connection = connection;
    }

}
