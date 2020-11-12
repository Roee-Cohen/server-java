package com.company;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;


public class ConnectionService {

    static HashMap<String, Socket> connections;

    public ConnectionService(){
        connections = new HashMap<String, Socket>();
    }

    public void AddConnection(String username, Socket con){
        connections.put(username, con);
    }

    public void DeleteConnection(String key) {
        connections.remove(key);
    }

    public void ChangeKey(String key, Socket con, String newKey){
        DeleteConnection(key);
        AddConnection(newKey, con);
    }
    
    public HashMap<String, Socket> GetAllConnections(){
        return connections;
    }
}
