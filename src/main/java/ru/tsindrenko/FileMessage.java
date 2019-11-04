package ru.tsindrenko;

public class FileMessage {
    private final String type = "FILE";
    private long size;
    private String fileType;
    private String fileName;
    private int sender_id;
    private int chatroom_id;
    private String sender_nickname;
    private String chatroom_nickname;

    public FileMessage(long size, String fileType, String fileName, int sender_id, String sender_nickname, int chatroom_id, String chatroom_nickname) {
        this.size = size;
        this.fileType = fileType;
        this.fileName = fileName;
        this.sender_id = sender_id;
        this.sender_nickname = sender_nickname;
        this.chatroom_id = chatroom_id;
        this.chatroom_nickname = chatroom_nickname;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilename() {
        return fileName;
    }

    public void setFilename(String filename) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public String getSender_nickname() {
        return sender_nickname;
    }

    public void setSender_nickname(String sender_nickname) {
        this.sender_nickname = sender_nickname;
    }

    public String getChatroom_nickname() {
        return chatroom_nickname;
    }

    public void setChatroom_nickname(String chatroom_nickname) {
        this.chatroom_nickname = chatroom_nickname;
    }
}
