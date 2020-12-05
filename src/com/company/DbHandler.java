package com.company;

import com.utils.MessagePacket;
import com.utils.Status;
import com.utils.User;
import com.utils.MessageType;

import com.mysql.jdbc.Driver;

import java.sql.*;

public class DbHandler {

    private static DbHandler instance = null;

    private Connection con;

    private DbHandler(){
        String url = "jdbc:mysql://localhost:3306/ServerDB";
        String user = "root";
        String password = "ABcd1234";

//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
//        } catch (Exception ex) {
//            System.out.println("failed to connect mysql driver");
//            System.out.println(ex);
//            System.exit(0);
//        }

        try{
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static DbHandler getInstance() {
        synchronized (Class.class)
        {
            if (instance == null)
                instance = new DbHandler();
        }

        return instance;
    }

    public Status Login(User u){
        Status s = Status.INTERNAL_SERVER_ERROR;

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM users WHERE username='%s' AND password='%s'", u.getUsername(), u.getPassword()));

            System.out.println("hey");
            if (rs.next()){
                s = Status.OK;
            }
            else{
                s = Status.NOTFOUND;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return s;
    }

    public Status Create(User u){
        Status s = Status.INTERNAL_SERVER_ERROR;

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM users WHERE username='%s' AND password='%s'",
                    u.getUsername(), u.getPassword()));

            if (rs.next()){
                s = Status.BAD_REQUEST;
            }
            else{
                Statement crt = con.createStatement();
                int aff = crt.executeUpdate(String.format("INSERT INTO users(username, password) VALUES('%s', '%s')",
                        u.getUsername(), u.getPassword()));

                if (aff > 0){
                    s = Status.OK;
                }
                else{
                    s = Status.FORBIDDEN;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return s;
    }

    public Status InsertMessage(MessagePacket msg, java.util.Date date){
        Status s = Status.INTERNAL_SERVER_ERROR;

        java.sql.Date sqlDate=new java.sql.Date(date.getTime());
        java.sql.Timestamp sqlTime=new java.sql.Timestamp(date.getTime());

        try {
            PreparedStatement ps=con.prepareStatement("INSERT INTO messages" +
                    "(sender, destination, content, type, purpose, time_stamp)" +
                    "values(?,?,?,?,?,?)");
            ps.setString(1,msg.sender);
            ps.setString(2, msg.dest);
            ps.setString(3, msg.content);
            ps.setString(4, msg.msgType.name());
            ps.setString(5, msg.msgPurpose.name());
            ps.setDate(6, Date.valueOf(sqlDate.toString() + sqlTime.toString()));
            int aff = ps.executeUpdate();

            if (aff > 0){
                s = Status.OK;
            }
            else{
                s = Status.BAD_REQUEST;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return s;
    }
  }
}
