package com.company;// A Java program for a Client

import com.google.gson.Gson;
import com.utils.Flags;
import com.utils.RequestFormat;
import com.utils.ResponseFormat;
import com.utils.User;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
//import java.util.Scanner;

public class Client
{
    // initialize socket and input output streams
    static int PORT = 9999;
    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private Gson g;

    // constructor to put ip address and port
    public Client(String address)
    {
        // establish a connection
        try
        {
            this.socket = new Socket(address, PORT);
            System.out.println("Connected...");

            // receive from the socket
            this.inStream = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));

            // sends to the socket
            this.outStream = new DataOutputStream(this.socket.getOutputStream());

            this.g = new Gson();
        }
        catch(IOException i)
        {
            System.out.println(i);
            System.out.println("Can not reach the server");
            System.exit(0);
        }

//        this.clientRoutine();
    }

//    private void clientRoutine() {
//        // string to read message from input
//
//        String command = "";
//        String data;
//        Scanner s = new Scanner(System.in);
//
//        // keep reading until SHUTDOWN command is input
////        do
////        {
////            System.out.println("Enter your message to Server");
////            data = s.nextLine();
////
////            System.out.println("Enter command to Server. Command List is: ");
////            command = s.nextLine().toUpperCase();
////
////            if (!Flags.values().)
////                System.out.println(command + " is not recognized as an internal or external command");
////            else
////                this.ExecCommand(command, data);
////
////        }
////        while (!command.equals(Settings.SHUTDOWN));
//
//
//        this.closeSocket("User asked to shutdown");
//    }

    private void closeSocket(String msg){

        System.out.println(msg);
        System.out.println("Closing Connection...");

        try {
            this.socket.close();
            this.outStream.close();
            this.inStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private ResponseFormat ExecCommand(Flags command, String data){

        if (command.equals(Flags.CREATE))
            return createUser(data);
        if (command.equals(Flags.LOGIN))
            return Login(data);

        return null;
    }

    private ResponseFormat createUser(String data){

        // get user
        String user = this.userJsonFromString(data);

        RequestFormat req = new RequestFormat(Flags.CREATE, user);
        String request = this.g.toJson(req);

        try {
            this.outStream.writeUTF(request);
            String response = this.inStream.readUTF();
            ResponseFormat res = this.g.fromJson(response, ResponseFormat.class);

            return res;
//            if (res.status.equals(Status.OK)){
//                System.out.println(res.data);
//                return res;
//            }
//            else{
//                System.out.println("error occurred when trying to create user");
//                return new ResponseFormat()
//            }
        }
        catch (SocketException i) { this.closeSocket("Server forced to shutdown"); }
        catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    private ResponseFormat Login(String data){

        // get user
        String user = this.userJsonFromString(data);

        RequestFormat req = new RequestFormat(Flags.LOGIN, user);
        String request = this.g.toJson(req);

        try {
            this.outStream.writeUTF(request);
            String response = this.inStream.readUTF();
            ResponseFormat res = this.g.fromJson(response, ResponseFormat.class);

            return res;
//            if (res.status.equals(Status.OK)){
//                System.out.println(res.data);
//            }
//            else{
//                System.out.println("error occurred when trying to logged in");
//            }

        }
        catch (SocketException i) { this.closeSocket("Server forced to shutdown"); }
        catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    private String userJsonFromString(String data){
        int index = data.indexOf(' ');
        String username = data.substring(0, index);
        String password = data.substring(index + 1);

        String encryptedPass = "";
        try { encryptedPass = Encryptor.encryptPass(password); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

        User u = new User(username, encryptedPass);
        return this.g.toJson(u);
    }

    public static void main(String args[])
    {
        new Client("127.0.0.1");
    }
}