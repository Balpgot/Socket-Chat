package ru.tsindrenko;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends Thread {

    private ServerSocket server;
    private List<ClientHandler> clientHandlerList;

    Server(int port, List<ClientHandler> clientHandlerList)
    {
       this.clientHandlerList = clientHandlerList;
       try {
           this.server = new ServerSocket(port);
           start();
           System.out.println("Сервер запущен в потоке " + currentThread().getName());
       }
       catch (IOException ex){
           ex.printStackTrace();
       }
    }

    @Override
    public void run() {
        Socket socket;
        while (true){
            try {
                System.out.println("Слушаем...");
                socket = server.accept();
                clientHandlerList.add(new ClientHandler(socket));
                System.out.println("Клиент подключился");
            }
            catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void stopServer(){
        try {
            server.close();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public Socket getNewClient(Socket socket){
        return socket;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }
}
