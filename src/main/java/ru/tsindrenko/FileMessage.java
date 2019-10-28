package ru.tsindrenko;

public class FileMessage {
    private final String type = "FILE";
    private long size;
    private String fileType;
    private String fileName;

    public FileMessage(long size, String fileType, String fileName) {
        this.size = size;
        this.fileType = fileType;
        this.fileName = fileName;
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
}
