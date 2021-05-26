package com.company;

import com.utils.*;

import java.sql.*;
import java.util.ArrayList;

public class DbHandler {

    private static DbHandler instance = null;

    private Connection connection;

    private DbHandler() {
        String url = "jdbc:sqlite:/C:\\sqlite\\sqlite-tools-win32-x86-3340000\\sqlite-tools-win32-x86-3340000\\chat_app.db";
//        String user = "root";
//        String password = "ABcd1234";

        try {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

//        try {
//            connection = DriverManager.getConnection(url, user, password);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
    }

    public static DbHandler getInstance() {
        synchronized (Class.class) {
            if (instance == null)
                instance = new DbHandler();
        }
        return instance;
    }

    public Status login(User user) {
        Status status = Status.INTERNAL_SERVER_ERROR;

        try {
            Statement statement = connection.createStatement();
            String query = String.format("SELECT * FROM users WHERE username='%s' AND password='%s'", user.getUsername(), user.getPassword());
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("hey");
            if (resultSet.next()) {
                status = Status.OK;
            } else {
                status = Status.NOTFOUND;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return status;
    }

    public Status create(User user) {
        Status status = Status.INTERNAL_SERVER_ERROR;

        try {
            Statement statement = connection.createStatement();
            String query = String.format("SELECT * FROM users WHERE username='%s' AND password='%s'",
                    user.getUsername(), user.getPassword());
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                status = Status.BAD_REQUEST;
            } else {
                Statement create = connection.createStatement();
                int aff = create.executeUpdate(String.format("INSERT INTO users(username, password) VALUES('%s', '%s')",
                        user.getUsername(), user.getPassword()));

                if (aff > 0) {
                    status = Status.OK;
                } else {
                    status = Status.FORBIDDEN;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return status;
    }

    public Status insertMessage(MessagePacket msg, java.util.Date date) {
        Status status = Status.INTERNAL_SERVER_ERROR;

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages" +
                    "(sender, destination, content, type, purpose, time_stamp)" +
                    "values(?,?,?,?,?,?)");
            preparedStatement.setString(1, msg.sender);
            preparedStatement.setString(2, msg.dest);
            preparedStatement.setString(3, msg.content);
            preparedStatement.setString(4, msg.msgType.name());
            preparedStatement.setString(5, msg.msgPurpose.name());
            preparedStatement.setTimestamp(6, sqlTime);
            int aff = preparedStatement.executeUpdate();

            if (aff > 0) {
                status = Status.OK;
            } else {
                status = Status.BAD_REQUEST;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return status;
    }

    public Status getMessages(RequestFormat req, ArrayList<Message> messages) {

        Status status = Status.INTERNAL_SERVER_ERROR;

        try {
            Statement statement = connection.createStatement();
            String[] usernames = req.data.split(",");
            String query = "SELECT * FROM messages WHERE (sender=\"" + usernames[0] +
                    "\" AND destination=\"" + usernames[1] + "\") OR (sender=\"" + usernames[1] +
                    "\" AND destination=\"" + usernames[0] + "\")";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String sender, destination, content, type, purpose;
                sender = resultSet.getString("sender");
                destination = resultSet.getString("destination");
                content = resultSet.getString("content");
                Message message = new Message(new MessagePacket(sender, content, destination,
                        MessageType.MESSAGE, MessagePurpose.UNICAST),"jjkklk");
                messages.add(message);
            }
            System.out.println(messages.size());
            if (messages.size() > 0) {
                status = Status.OK;
            } else {
                status = Status.NOTFOUND;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return status;
    }
}

