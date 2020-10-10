package com.company;

import com.google.gson.Gson;
import com.utils.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

class ConnectionHandler implements Runnable
{
    private ConnectionService connectionService;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private Gson g;

    // Constructor
    public ConnectionHandler(Socket socket, ConnectionService connectionService)
    {
        this.socket = socket;
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
        return new ResponseFormat(Status.OK, "user created");
    }

    private ResponseFormat Login(RequestFormat req){
        System.out.println("Login check on server side");

        User u = this.g.fromJson(req.data, User.class);
        System.out.println("user login " + u.getUsername() + " " + u.getPassword());

        // validate user


        // generate login packet
        return new ResponseFormat(Status.NOTFOUND, "successfully logged in");
    }
}