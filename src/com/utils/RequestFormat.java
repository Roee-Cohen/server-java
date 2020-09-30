package com.utils;

public class RequestFormat {

    public Flags command;
    public String data;

    public RequestFormat(Flags command, String data){
        this.command = command;
        this.data = data;
    }
}
