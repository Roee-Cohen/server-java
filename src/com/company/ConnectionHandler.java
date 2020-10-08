package com.company;

import com.google.gson.Gson;
import com.utils.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

class ConnectionHandler implements Runnable
{
    private DbHandler dbHandler;
    private ConnectionService connectionService;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private Gson g;

    // Constructor
    public ConnectionHandler(Socket socket, ConnectionService connectionService, DbHandler dbHandler)
    {
        this.socket = socket;
        this.dbHandler = dbHandler;
        this.connectionService = connectionService;

        System.out.println(Thread.currentThread().getName() + connectionService.GetAllConnections());

        try
        {
            // receive from the socket
            inStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));

            // send to the socket
            outStream = new DataOutputStream(this.socket.getOutputStream());

            this.g = new Gson();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            String request = this.inStream.readUTF();
            RequestFormat req = this.g.fromJson(request, RequestFormat.class);

            while (!req.command.equals(Flags.SHUTDOWN)) {
                try
                {
                    System.out.println("command: " + req.command);
                    System.out.println("data: " + req.data);

                    // execute command
                    ResponseFormat res = this.ExecCommand(req);
                    String response = this.g.toJson(res);
                    this.outStream.writeUTF(response);

                    // receive request
                    request = this.inStream.readUTF();
                    req = this.g.fromJson(request, RequestFormat.class);
                }
                catch (SocketException i){
                    System.out.println(i);
                    this.closeSocket("User forced to shutdown");
                    break;
                }
            }

            if (req.command.equals(Flags.SHUTDOWN))
                this.closeSocket("User asked to shutdown");

        }
        catch(IOException i)
        {
            closeSocket("IO Exception");
            System.out.println(i);
        }
    }

    private void closeSocket(String msg){

        System.out.println(msg);
        System.out.println("Closing Connection...");

        try {
            this.connectionService.DeleteConnection(this.socket);
            this.socket.close();
            this.inStream.close();
            this.outStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseFormat ExecCommand(RequestFormat req){

        if (req.command.equals(Flags.REGISTER))
            return this.createUser(req);

        if (req.command.equals(Flags.CREATE))
            return this.createUser(req);

        if (req.command.equals(Flags.LOGIN))
            return this.Login(req);

        return null;
    }

    private ResponseFormat createUser(RequestFormat req){
        System.out.println("create user is called");
        User u = this.g.fromJson(req.data, User.class);
        System.out.println(u.getUsername() + " " + u.getPassword());

        // create user in db
        Status s = this.dbHandler.Create(u);
        System.out.println("Create operation status: " + s.name());

        return new ResponseFormat(s, "user created");
    }

    private ResponseFormat Login(RequestFormat req){
        System.out.println("Login check on server side");

        User u = this.g.fromJson(req.data, User.class);
        System.out.println("user login check " + u.getUsername() + " " + u.getPassword());

        // validate user
        Status s = this.dbHandler.Login(u);
        System.out.println("Login operation status: " + s.name());

        // generate login packet
        return new ResponseFormat(s, "successfully logged in");
    }
}