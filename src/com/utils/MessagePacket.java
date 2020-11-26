package com.utils;

public class MessagePacket {

    public String sender;
    public String content;
    public String dest;
    public MessageType msgType;
    public MessagePurpose msgPurpose;

    public MessagePacket(String sender, String content, String dest, MessageType msgType, MessagePurpose msgPurpose){
        this.sender = sender;
        this.content = content;
        this.dest = dest;
        this.msgPurpose = msgPurpose;
        this.msgType = msgType;
    }

}


