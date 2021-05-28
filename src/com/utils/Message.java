package com.utils;

public class Message {

    public MessagePacket message;
    public String date;

    public Message(MessagePacket msg, String date){
        this.message = msg;
        this.date = date;
    }
}
