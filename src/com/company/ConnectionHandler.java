package com.company;

import com.google.gson.Gson;
import com.utils.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;

class ConnectionHandler implements Runnable {
    private String connectionID;
    private ConnectionService connectionService;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private Gson g;


    // Constructor
    public ConnectionHandler(Socket socket, String connectionID, ConnectionService connectionService) {
        this.socket = socket;
        this.connectionService = connectionService;
        this.connectionID = connectionID;

        System.out.println(Thread.currentThread().getName() + connectionService.GetAllConnections());

        try {
            // receive from the socket
            inStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));

            // send to the socket
            outStream = new DataOutputStream(this.socket.getOutputStream());

            this.g = new Gson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String request = this.inStream.readUTF();
            RequestFormat req = this.g.fromJson(request, RequestFormat.class);

            while (!req.command.equals(Commends.SHUTDOWN)) {
                try {
                    System.out.println("command: " + req.command);
                    System.out.println("data: " + req.data);

                    // execute command
                    ResponseFormat res = execCommand(req);
                    String response = g.toJson(res);

                    if (!req.command.equals(Commends.MESSAGE))
                        outStream.writeUTF(response);

                    if (req.command.equals(Commends.LOG_OUT))
                        closeSocket(req.data + " logged out");

                    // receive request
                    request = inStream.readUTF();
                    req = g.fromJson(request, RequestFormat.class);
                    System.out.println("Flags: " + req.command.name());
                } catch (SocketException i) {
                    System.out.println(i);
                    closeSocket("User forced to shutdown");
                    break;
                }
            }

            if (req.command.equals(Commends.SHUTDOWN))
                closeSocket("User asked to shutdown");

        } catch (IOException i) {
            closeSocket("IO Exception");
            System.out.println(i);
        }
    }

    private void closeSocket(String msg) {

        System.out.println(msg);
        System.out.println("Closing Connection...");
        System.out.println(this.connectionService.GetAllConnections());

        try {
            this.connectionService.DeleteConnection(this.connectionID);
            this.socket.close();
            this.inStream.close();
            this.outStream.close();

            System.out.println(this.connectionService.GetAllConnections());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseFormat execCommand(RequestFormat req) {

        if (req.command.equals(Commends.REGISTER))
            return createUser(req);

        if (req.command.equals(Commends.CREATE))
            return createUser(req);

        if (req.command.equals(Commends.LOGIN))
            return login(req);

        if (req.command.equals(Commends.MESSAGE))
            return messagePass(req);

        if (req.command.equals(Commends.LOAD_MESSAGES))
            return getChatMessages(req);

        if(req.command.equals(Commends.LOAD_CONTACTS))
            return getContacts(req);

        if(req.command.equals(Commends.LOG_OUT))
            return bye(req);

        return null;
    }

    private ResponseFormat bye(RequestFormat req) {
        return new ResponseFormat(Status.OK, "bye");
    }

    private ResponseFormat getContacts(RequestFormat req) {
        return DbHandler.getInstance().getContacts(req.data, g);
    }

    private ResponseFormat getChatMessages(RequestFormat req) {
        return DbHandler.getInstance().getMessages(req, g);
    }

    private ResponseFormat createUser(RequestFormat req) {
        System.out.println("create user is called");
        User u = this.g.fromJson(req.data, User.class);
        System.out.println(u.getUsername() + " " + u.getPassword());

        // create user in db
        Status s = DbHandler.getInstance().create(u);
        System.out.println("Create operation status: " + s.name());

        return new ResponseFormat(s, "user created");
    }

    private ResponseFormat login(RequestFormat req) {
        System.out.println("Login check on server side");

        User user = g.fromJson(req.data, User.class);
        System.out.println("user login check " + user.getUsername() + " " + user.getPassword());

        // validate user
        Status s = DbHandler.getInstance().login(user);
        System.out.println("Login operation status: " + s.name());

        connectionService.ChangeKey(connectionID, socket, user.getUsername());
        connectionID = user.getUsername();

        // generate login packet
        return new ResponseFormat(s, "successfully logged in");
    }

    private ResponseFormat messagePass(RequestFormat req) {
        System.out.println("Server got message");
        MessagePacket msg = g.fromJson(req.data, MessagePacket.class);

        if (msg.msgPurpose == MessagePurpose.BROADCAST)
            return broadcast(msg);
        if (msg.msgPurpose == MessagePurpose.UNICAST)
            return unicast(msg);

        return null;
    }

    private ResponseFormat broadcast(MessagePacket message) {
        System.out.println("Broadcast");

        java.util.Date date = new java.util.Date();
        String carry = g.toJson(new Message(message, date.toString()));
        DbHandler.getInstance().insertMessage(message, date);

        ResponseFormat res = new ResponseFormat(Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR.name());

        HashMap<String, Socket> connections = connectionService.GetAllConnections();

        for (String username : connections.keySet()) {
            if (username.equals(message.sender)) {

                // get connections to send message
                Collection<Socket> dest = connections.values();
                //dest.remove(this.socket);

                // send for each connection
                for (Socket con : dest) {
                    try {
                        DataOutputStream distributor = new DataOutputStream(con.getOutputStream());
                        distributor.writeUTF(carry);

                        res.status = Status.OK;
                        res.data = Status.OK.name();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return res;
    }

    private ResponseFormat unicast(MessagePacket messagePacket) {
        System.out.println("Unicast");

        java.util.Date date = new java.util.Date();
        String carry = g.toJson(new Message(messagePacket, date.getHours() + ":" + date.getMinutes()));
        DbHandler.getInstance().insertMessage(messagePacket, date);

        ResponseFormat response = new ResponseFormat(Status.INTERNAL_SERVER_ERROR, Status.INTERNAL_SERVER_ERROR.name());

        Socket con = connectionService.getSocket(messagePacket.dest);

        if(con != null) {
            try {
                DataOutputStream distributor = new DataOutputStream(con.getOutputStream());
                distributor.writeUTF(carry);

                response.status = Status.OK;
                response.data = Status.OK.name();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}