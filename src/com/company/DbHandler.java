package com.company;

import com.utils.Status;
import com.utils.User;

import com.mysql.jdbc.Driver;

import java.sql.*;

public class DbHandler {

    private Connection con;

    DbHandler(){
        String url = "jdbc:mysql://localhost:3306/ServerDB";
        String user = "user";
        String password = "user";

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
}
