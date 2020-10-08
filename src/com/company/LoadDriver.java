package com.company;

import java.sql.*;
import com.mysql.jdbc.Driver;

// Notice, do not import com.mysql.cj.jdbc.*
// or you will have problems!

public class LoadDriver {
    public static void main(String[] args) {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

//            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        Connection conn = null;

        try {
            String url = "jdbc:mysql://localhost:3306/ServerDB";
            String user = "user";
            String password = "user";
            conn =
                    DriverManager.getConnection(url, user, password);

            // Do something with the Connection

            System.out.println(conn);

            Statement stmt= conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username='aviv' AND password='otish'");
//            ResultSet rs = stmt.executeQuery("select username, password from Users");

//            rs.first();
//            System.out.println(rs.getString(1) + rs.getString(2));
//            System.out.println(rs.getArray(1));
//            System.out.println(rs.first());
//            System.out.println(rs.getString(1));

            System.out.println(rs.next());
            System.out.println(rs.getString("username"));
//            while(rs.next()){
//                System.out.println(rs.getString("username") + " " + rs.getString("password"));
//            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
}
