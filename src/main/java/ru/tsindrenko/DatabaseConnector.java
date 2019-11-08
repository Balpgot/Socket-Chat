package ru.tsindrenko;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public HashMap<Integer,String> getUsersMap(){
        ResultSet users = executeQuery("SELECT id,nickname FROM users WHERE is_deleted=0");
        HashMap<Integer,String> userList = new HashMap<>();
        try{
            while (users.next()) {
                userList.put(users.getInt(1),users.getString(2));
            }
            users.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public HashSet<User> getUsers(){
        ResultSet users = executeQuery("SELECT * FROM users WHERE is_deleted=0");
        HashSet<User> userList = new HashSet<>();
        User user;
        try{
            while (users.next()) {
                user = new User(
                        users.getInt(1),
                        users.getString(2),
                        users.getString(3),
                        users.getString(4),
                        users.getString(5));
                user.setChatRooms(getUserChatroomsId(user.getId()));
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
                chatRoom.getParticipants().addAll(getChatroomParticipantsId(chatRoom.getId()));
                chatRoom.setAdmin_id(resultSet.getInt(3));
                chatRoom.setIsDialog(resultSet.getBoolean(5));
                chatRoomList.add(chatRoom);
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return chatRoomList;
    }

    public List<Integer> getChatroomsId(){
        ResultSet resultSet = executeQuery("SELECT id FROM chatrooms WHERE is_deleted=0");
        List<Integer> chatRoomList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                chatRoomList.add(resultSet.getInt(1));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return chatRoomList;
    }

    public List<Integer> getUsersId(){
        ResultSet resultSet = executeQuery("SELECT id FROM users WHERE is_deleted=0");
        List<Integer> usersList = new ArrayList<>();
        try{
            while (resultSet.next()) {
                usersList.add(resultSet.getInt(1));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return usersList;
    }

    public User getUser(int id){
        ResultSet userDB = executeQuery("SELECT * FROM users WHERE id="+id);
        User user = null;
        try {
             if(userDB.next()){
                 user = new User(userDB.getInt(1),
                         userDB.getString(2),
                         userDB.getString(3),
                         userDB.getString(4),
                         userDB.getString(5));
                 user.setOnline(userDB.getBoolean(6));
                 userDB.close();
             }
             else{
                 userDB.close();
                 return null;
             }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return user;
    }

    public User getUser(String nickname){
        ResultSet userDB = executeQuery("SELECT * FROM users WHERE nickname='"+nickname+"'");
        User user = null;
        try {
            if(userDB.next()){
                user = new User(userDB.getInt(1),
                        userDB.getString(2),
                        userDB.getString(3),
                        userDB.getString(4),
                        userDB.getString(5));
                user.setOnline(userDB.getBoolean(6));
                userDB.close();
            }
            else {
                userDB.close();
                return null;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            ex.getMessage();
        }
        return user;
    }

    public ChatRoom getChatroom(int id){
        ResultSet chatroomDB = executeQuery("SELECT * FROM chatrooms WHERE id='"+id+"'");
        ChatRoom chatRoom = null;
        try {
            if(chatroomDB.next()){
                chatRoom = new ChatRoom(chatroomDB.getInt(1),
                        chatroomDB.getString(2),
                        chatroomDB.getInt(3));
                chatRoom.getParticipants().addAll(getChatroomParticipantsId(chatRoom.getId()));
                chatRoom.getBlacklist().addAll(getChatroomBlacklistId(chatRoom.getId()));
                chatRoom.setAdmin_id(chatroomDB.getInt(3));
                chatRoom.setIsDialog(chatroomDB.getBoolean(5));
                chatroomDB.close();
            }
            else{
                chatroomDB.close();
                return null;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return chatRoom;
    }

    public ChatRoom getChatroom(String name){
        ResultSet chatroomDB = executeQuery("SELECT * FROM chatrooms WHERE name='"+name+"'");
        ChatRoom chatRoom = null;
        try {
            if(chatroomDB.next()){
                chatRoom = new ChatRoom(chatroomDB.getInt(1),
                        chatroomDB.getString(2),
                        chatroomDB.getInt(3));
                chatRoom.getParticipants().addAll(getChatroomParticipantsId(chatRoom.getId()));
                chatRoom.getBlacklist().addAll(getChatroomBlacklistId(chatRoom.getId()));
                chatRoom.setAdmin_id(chatroomDB.getInt(3));
                chatRoom.setIsDialog(chatroomDB.getBoolean(5));
                chatroomDB.close();
            }
            else{
                chatroomDB.close();
                return null;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
            ex.getMessage();
        }
        return chatRoom;
    }

    public List<Integer> getUserChatroomsId(int id){
        ResultSet resultSet = executeQuery("SELECT chatrooms.id " +
                "FROM chatrooms,chatroom_users " +
                "WHERE chatrooms.is_deleted=0 AND chatroom_users.user_id =" + id + " AND chatroom_users.chatroom_id = chatrooms.id");
        List<Integer> chatRoomList = new ArrayList<>();

        try{
            while (resultSet.next()) {
                chatRoomList.add(resultSet.getInt(1));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            ex.getErrorCode();
        }
        return chatRoomList;
    }

    public HashSet<Integer> getChatroomParticipantsId(int id){
        ResultSet resultSet = executeQuery("SELECT DISTINCT chatroom_users.user_id FROM chatroom_users,users WHERE " +
                "chatroom_users.is_participant = 1 AND chatroom_users.chatroom_id =" + id +" AND chatroom_users.user_id IN (SELECT users.id FROM users WHERE users.is_deleted = 0)");
        HashSet<Integer> userList = new HashSet<>();
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

    public HashSet<Integer> getChatroomBlacklistId(int id){
        ResultSet resultSet = executeQuery("SELECT DISTINCT chatroom_users.user_id FROM chatroom_users,users WHERE " +
                "chatroom_users.is_blacklisted = 1 AND chatroom_users.chatroom_id =" + id +" AND chatroom_users.user_id IN (SELECT users.id FROM users WHERE users.is_deleted = 0)");
        HashSet<Integer> userList = new HashSet<>();
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

    public HashSet<Integer> getChatroomModeratorId(int id){
        ResultSet resultSet = executeQuery("SELECT DISTINCT chatroom_users.user_id FROM chatroom_users,users WHERE " +
                "chatroom_users.is_moderator = 1 AND chatroom_users.chatroom_id =" + id +" AND chatroom_users.user_id IN (SELECT users.id FROM users WHERE users.is_deleted = 0)");
        HashSet<Integer> userList = new HashSet<>();
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

    public HashMap<String,Integer> getChatroomsModeratedByUser(int id){
        ResultSet resultSet = executeQuery("SELECT DISTINCT chatrooms.name, chatrooms.id FROM chatroom_users,chatrooms WHERE " +
                "chatroom_users.is_moderator = 1 AND chatroom_users.user_id =" + id +" AND chatrooms.id=chatroom_users.chatroom_id");
        HashMap<String,Integer> chatroomsList = new HashMap<>();
        try{
            while (resultSet.next()) {
                chatroomsList.put(resultSet.getString(1),resultSet.getInt(2));
            }
            resultSet.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("MODERATED: " + chatroomsList);
        return chatroomsList;
    }

    public void addUser(User user){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO users (users.login,users.password,users.nickname,users.avatar,users.is_deleted) VALUES ('" +
                    user.getLogin() + "','" + user.getPassword() + "','" +
                    user.getNickname() + "','" + user.getPhoto()+ "','0')");
            ResultSet resultSet = executeQuery("SELECT id FROM users WHERE login='"+user.getLogin()+"'");
            resultSet.next();
            user.setId(resultSet.getInt(1));
            statement.close();
            System.out.println("DB: " + user);
            Main.userList.add(user);

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public boolean addChatroom(ChatRoom chatRoom){
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
            Main.chatRoomMap.put(chatRoom.getId(),chatRoom);
            for (Integer participant_id:chatRoom.getParticipants_id()) {
                addUserToChatroom(chatRoom.getId(),participant_id,false);
            }
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void addUserToChatroom(int chatroomId, int userId, boolean is_moderator){
        Statement statement;
        try {
            statement = connection.createStatement();
            String query = "INSERT INTO chatroom_users (chatroom_id,user_id,is_moderator,is_blacklisted,is_participant) VALUES ('" +
                    chatroomId + "','" + userId + "','";
            if(is_moderator){
                query+="1'";
            }
            else
                query+="0'";
            query+=",'0','1')";
            System.out.println(query);
            Main.chatRoomMap.get(chatroomId).getParticipants().add(userId);
            statement.executeUpdate(query);
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void banUser(int user_id, int chatroom_id){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE chatroom_users SET is_moderator=0,is_blacklisted=1,is_participant=0 WHERE chatroom_id="+
                    chatroom_id+" AND user_id="+user_id+"");
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void unbanUser(int user_id, int chatroom_id){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE chatroom_users SET is_moderator=0,is_blacklisted=0,is_participant=1 WHERE chatroom_id="+
                    chatroom_id+" AND user_id="+user_id+"");
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void makeUserModerator(int user_id, int chatroom_id){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE chatroom_users SET is_moderator=1 WHERE chatroom_id="+
                    chatroom_id+" AND user_id="+user_id);
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void makeUserOnline(int user_id, boolean is_online){
        Statement statement;
        try {
            StringBuffer query = new StringBuffer("UPDATE users SET is_online='");
            statement = connection.createStatement();
            if(is_online){
                query.append("1' ");
            }
            else
                query.append("0' ");
            query.append("WHERE id='");
            query.append(user_id);
            query.append("'");
            statement.executeUpdate(query.toString());
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public boolean updateUser(User user){
        Statement statement;
        try {
            System.out.println(user);
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE users SET login='" + user.getLogin() +
                "', password='" + user.getPassword() + "',nickname='" + user.getNickname() +
                "', avatar='" + user.getPhoto() + "' WHERE id="+user.getId());
            statement.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }


    public boolean deleteUser(int id){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE users SET is_deleted='1' WHERE id='"+id+"'");
            statement.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteChatroom(int id){
        Statement statement;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE chatrooms SET is_deleted='1' WHERE id='"+id+"'");
            statement.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
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
