package com.utils;

public class MessagePacket {

    public int roomID;
    public String dest;
    public String sender;
    public String content;

    public MessagePacket(String sender, String content, String dest, int roomID){
        this.sender = sender;
        this.content = content;
        this.dest = dest;
        this.roomID = roomID;
    }

}
