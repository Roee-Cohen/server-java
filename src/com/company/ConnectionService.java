package com.company;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionService {

    static List<Socket> connections;

    public ConnectionService(){
        connections = new ArrayList<Socket>();
    }

    public void AddConnection(Socket con){
        connections.add(con);
    }

    public void DeleteConnection(Socket con) {
        connections.remove(con);
    }

    public List<Socket> GetAllConnections(){
        return connections;
    }
}
