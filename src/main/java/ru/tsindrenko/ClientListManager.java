package ru.tsindrenko;

import java.util.List;

public class ClientListManager extends Thread {

    private List<ClientHandler> usersList;
    private List<User> users;

    ClientListManager(List<ClientHandler> usersList, List<User> users){
        this.usersList = usersList;
        this.users = users;
    }

    ClientListManager(List<ClientHandler> usersList){
        this.usersList = usersList;
        start();
    }


    private synchronized void removeInactiveUsers(){
        for(int i = 0; i<usersList.size(); i++){
            if(!usersList.get(i).isActive()){
                usersList.remove(i);
                i--;
            }
        }
    }

    private synchronized void removeBrokenConnections(){
        for (ClientHandler handler:usersList) {
            if(handler.getClientSocket().isClosed()){
                handler.endSession();
            }
        }
    }

    @Override
    public void run(){
        while (true){
            synchronized (currentThread()) {
                removeInactiveUsers();
                removeBrokenConnections();
                System.out.println("Мой поток " + currentThread().getName());
                System.out.println("Активных юзеров " + usersList.size());
                try {
                    System.out.println("I Sleep");
                    wait(10000);
                    System.out.println("Real shit");
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
