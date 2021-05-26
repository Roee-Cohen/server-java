package com.company;

import java.net.*;
import java.io.*;

public class Server {
    //initialize socket and input stream
    static int PORT = 7800;
    private Socket connection = null;
    private ConnectionService connectionService;
    private DbHandler dbHandler;
    private ServerSocket serverSocket = null;

    // constructor
    public Server() {
        // starts server and waits for a connection
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Server started listening on port " + PORT);
            this.dbHandler = DbHandler.getInstance();
            this.connectionService = new ConnectionService();

            while (true) {
                System.out.println("Waiting for a client...");
                this.connection = this.serverSocket.accept();
                System.out.println("User Connected");

                String connectionID = "NULL" + this.connectionService.GetAllConnections().size();
                this.connectionService.AddConnection(connectionID, this.connection);

                new Thread(new ConnectionHandler(this.connection, connectionID, this.connectionService)).start();
            }
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}