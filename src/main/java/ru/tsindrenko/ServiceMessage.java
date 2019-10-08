package ru.tsindrenko;

public class ServiceMessage {
    private final String type = "SERVICE";
    private String header;
    private String status;

    public ServiceMessage(String header, String status) {
        this.header = header;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
